package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.dataobject.OrderDO;
import com.miaoshaproject.dataobject.SequenceDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        // Check item, user, amount is legal or not
        ItemModel itemModel= itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }

        if (promoId != null) {
            if(promoId != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            } else if (itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"活动还没有开始");
            }
        }

        // Book --> reduce item stock, Pay --> reduce item stock
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBussinessError.STOCK_NOT_ENOUGH);
        }

        // Write book into record
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }

        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        // Book number

        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        itemService.increaseSales(itemId, amount);

        // To front end

        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo() {
        StringBuilder stringBuilder = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentName();
        sequenceDO.setCurrentName(sequenceDO.getCurrentName() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); ++i) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }

        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
