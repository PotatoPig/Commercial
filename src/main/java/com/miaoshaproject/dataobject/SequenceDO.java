package com.miaoshaproject.dataobject;

public class SequenceDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.current_name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    private Integer currentName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.step
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    private Integer step;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.name
     *
     * @return the value of sequence_info.name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.name
     *
     * @param name the value for sequence_info.name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.current_name
     *
     * @return the value of sequence_info.current_name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public Integer getCurrentName() {
        return currentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.current_name
     *
     * @param currentName the value for sequence_info.current_name
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public void setCurrentName(Integer currentName) {
        this.currentName = currentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.step
     *
     * @return the value of sequence_info.step
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public Integer getStep() {
        return step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.step
     *
     * @param step the value for sequence_info.step
     *
     * @mbg.generated Thu Apr 18 14:30:36 CST 2019
     */
    public void setStep(Integer step) {
        this.step = step;
    }
}