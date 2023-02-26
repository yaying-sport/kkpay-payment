package com.kaiserkalep.bean;

/**
 * @Auther: Jack
 * @Date: 2020/8/13 15:47
 * @Description: 用户登录
 */
public class UserData {


    /**
     * username : mango2020
     * token : da634862-5ac6-40bb-ab03-8337bd4ba7b7
     * phone : 124xxxx0000
     * amount : 0.0
     * sk : 81a491977c404b2b87536527038be677
     * me : tkn1GH2/v7hLv0JJCOCmfvsk1HFmUdZReKnxfOVsSs4mBGqxCBp+xISSVcaG0ABBUlEahd96D1dXK9gRlDs8hzhLdho3utH1jUY9sLDcEUgvhpShPd/tQBPE3AK/rVxlF1dcl3j2y4Ns4qzN5DlHfNOmqkQ1+ywG9x+7UTpoNBLN9O3iFZogRRrVYviHPeAbJj1Se4mAgRVy58R6Lipe2oJlA45UrsT3tR9gqKkv2oGYFTsOsW3orpTm+UNlPOypWF4BugFJ5EOHTMbmjdMeYi8pSU5Cvr57wgFq7mBNb1mbkAroa5xBBQZ5H7zvRDxo9ROrR0C9RIs/bI+GTeJtEZfYr4ROi7v0znYxGzl52Z5uul8w4/jSMZnwNoTgzfy8BO6/ZaQUryO/fUw3yXnk+ZX+agSyUgU7EvaG1AYpyFZPW9Hi970bpObn4ucVd7cutwd3fVL3jiOO14bW9GZamWn1VsubDqD0gnvE92aEdJcNkYOdzIsLOh46RNgXNQEDFSllUekbEXtJZD8I5RGnSyVkRm92Kf9NAjSvm7M+QAFoOmvAPXiDsWsG+p9FRC8MJTz7gEH+T51CR+sYcRtft6iSR+MKVNiKOmmlainh5nuz9XmDY8YYpMMt6nys7r6197ZTMkkO5MIQPM+HDHVmPrAFKMx2jMTYnqjSxu/OedIUI8SAMPyt8D5BYFOc42jndw83bUK9o2RgRy9DJSj/OPwyy+YEjqKkpo4+P/ivzg36Xt4SejJ7Nqy28iPcVSe+AZgkk6Q3oTKD42K7t4iLb7g9tIMWfxuiVg2Hsuxk3QWpTqOMlTnkGgULyPKVrheS5YQN9/OcHTVQk+gDcJLk2s4jf3q8DmVAeAXrkiDhDhPbgmPiUBs7hRSZKCmdyBnJxrUmIGKsfaqNijMJxvv9+MO/SULDO0P+b71D+NecKsSkpui7OaE52DQGF4A90AQ+SGG3WxhJPBWdYpb4JxyQonqtstnIZZT4BUAGW+b4YZ4M87uQj7B7j4Bn07mcrpp8ULKPxVyygVanPf2rMxDjivfWH3cMgYituHuPtJhGsoDFYkmNW9616P5/YX/GCoUzGHjxE6KdBtB4QY+i7ls5PoXaNhNJQeesZ8kOGGwfQ4gcJbC0QhNGfrRZWzAXFaV+0sBJ3UyOic5Y6R04P0ex4uXOXV4Mt72g+4aaPp/EINuAzloVqtcG9FEfrgHPcVA9qy3GkyHodePMXYCBQ5mxe+kE7ovwC64yE7Jixn3R+D1nOSZjNQHCcKwwpGM2c9qc3IbZ2/Qlf1AC8kyWTAhZWP4X2wJDi5at69C7OzpQXJNqAGVxhgmMIbgMffbWr6EgjDKztql8xI6Mwl5NgtMhN7fwX+acTgh29nqVxy7yzejGqWT6/ApmKTpztgdoEpbdXIEqIeQWVlZwhxugj/kqmQ74Cqjgeya5VULT3OENjXx1LeB0jNtAzy5TAROZWpXvbVzhTHeqEPMGRqCboLgqUvHGVDSDZrXR85U05k8MkeN0NXZHuLRa29YaTlD1sI2eaFBHdNMgJAhoYZ0hVDMdeYO6Ock6AqOxidAahQI1EzFzi6/sjc6sEbIz02TDmtLR
     */

    /**
     * 会员账号
     */
    private String username;
    /**
     * token
     */
    private String token;
    /**
     * phone
     */
    private String phone;
    /**
     * 账户金额
     */
    private double amount;
    /**
     * 签名
     */
    private String sk;
    /**
     * rememberMe=1时传入
     */
    private String me;
    /**
     * 用户头像
     */
    private String avatarFile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(String avatarFile) {
        this.avatarFile = avatarFile;
    }
}
