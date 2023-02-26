<template>
  <div class="app_home" v-loading="loading">
    <div class="top_right">
      <div class="right_content">
        <!-- <img src="../assets/images/top1.png" alt="" /> -->
        <div class="line_text">登录名：{{ formData.username }}</div>
        <div class="line_text">目前汇率：1KKP = 1RMB</div>
        <div class="line_text">商户名称：{{ formData.paymentName }}</div>
      </div>
      <div class="right_content">
        <!-- <img src="../assets/images/top3.png" alt="" /> -->
        <div class="text_right">钱包余额</div>
        <div class="num_right">
          {{ formData.amount }}<span class="unit">KKP</span>
        </div>
      </div>
      <div class="right_content">
        <!-- <img src="../assets/images/top3.png" alt="" /> -->
        <div class="text_right">旗下会员人数</div>
        <div class="num_right">
          {{ formData.memberNumber }}
        </div>
        <div class="member_detail" @click="goMemberDetail">查看会员详情></div>
      </div>
    </div>
    <div class="head_time">{{ moment().format("YYYY-MM-DD") }}</div>
    <div class="content_box">
      <div class="card_box">
        <div class="card_title">
          平台上分({{ formData.platformAddNumber }}笔)
        </div>
        <div class="card_text">
          <div>
            <span class="num">{{ formData.platformAddAmount }} </span
            ><span class="unit">KKP</span>
          </div>
          <img src="../assets/images/img3.png" alt="" />
        </div>
      </div>
      <div class="card_box">
        <div class="card_title">
          平台下分({{ formData.platformSubNumber }}笔)
        </div>
        <div class="card_text">
          <div>
            <span class="num">{{ formData.platformSubAmount }} </span
            ><span class="unit">KKP</span>
          </div>
          <img src="../assets/images/img2.png" alt="" />
        </div>
      </div>
      <div class="card_box">
        <div class="card_title">会员上分({{ formData.memberAddNumber }}笔)</div>
        <div class="card_text">
          <div>
            <span class="num">{{ formData.memberAddAmount }} </span
            ><span class="unit">KKP</span>
          </div>
          <img src="../assets/images/img4.png" alt="" />
        </div>
      </div>
      <div class="card_box">
        <div class="card_title">会员下分({{ formData.memberSubNumber }}笔)</div>
        <div class="card_text">
          <div>
            <span class="num">{{ formData.memberSubAmount }}</span
            ><span class="unit">KKP</span>
          </div>
          <img src="../assets/images/img1.png" alt="" />
        </div>
      </div>
    </div>
    <el-dialog
      :title="title"
      :visible.sync="open"
      :width="'600px'"
      append-to-body
    >
      <el-table v-loading="maskloading" :data="typeList">
        <el-table-column
          label="会员钱包地址"
          align="center"
          prop="walletAddress"
        />
        <el-table-column label="注册时间" align="center" prop="registerTime">
          <template slot-scope="scope">
            {{ moment(scope.row.registerTime).format('YYYY/MM/DD HH:mm:ss') }}
          </template>
        </el-table-column>
      </el-table>

      <!-- <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageDomain.pageNum"
        :limit.sync="queryParams.pageDomain.pageSize"
        @pagination="getList"
      /> -->
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="open = false">确 定</el-button>
        <!-- <el-button @click="cancel">取 消</el-button> -->
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { paymentDashBoardTotal, searchPaymentMember } from "@/api/home";
import QRCode from "qrcodejs2";
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.1",
      loading: false,
      maskloading: false,
      qImg: "",
      formData: {},
      title: "",
      //弹窗开关
      open: false,
      typeList: [],
      // 总条数
      total: 0,
      queryParams: {
        pageDomain: {
          pageNum: 1,
          pageSize: 10,
        },
      },
    };
  },
  created() {
    this.agentMemberAmountTotal();
  },
  methods: {
    goMemberDetail() {
      this.open = true;
      this.getList();
    },
    getList() {
      this.maskloading = true;
      searchPaymentMember()
        .then((response) => {
          if (response.data) {
            console.log("response :>> ", response);
            let arr = response.data;
            this.total = response.data.total;
            this.typeList = arr;
          }
          this.maskloading = false;
        })
        .catch(() => {
          this.maskloading = false;
        });
    },
    qrcode() {
      //生成二维码
      let qrcode = new QRCode("qrcode", {
        width: 120,
        height: 120,
        text: this.formOne.qrCode, // 二维码内容
        render: "canvas", // 设置渲染方式（有两种方式 table和canvas，默认是canvas）
        background: "#f0f", // 背景色
        foreground: "#ff0", // 前景色
      });
      let myCanvas = document.querySelector("canvas");
      let img = myCanvas.toDataURL("image/png");
      this.qImg = img;
    },
    agentMemberAmountTotal() {
      // this.loading = true;
      paymentDashBoardTotal()
        .then((response) => {
          if (response.data) {
            this.formData = response.data;
            this.loading = false;
          }
        })
        .catch(() => {
          this.loading = false;
        });
    },
    saveCode(selector, name) {
      // 通过选择器获取img元素，
      var img = document.querySelector(selector);
      // 将图片的src属性作为URL地址
      var url = img.src;
      var a = document.createElement("a");
      a.download = name || "image";
      a.href = url;
      a.click();
      this.$modal.msgSuccess("保存成功");
    },
    /** 复制代码成功 */
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功");
    },
  },
};
</script>

<style scoped lang="scss">
.app_home {
  box-sizing: border-box;
  background-color: #ebeff3;
  min-height: calc(100vh - 85px);
  padding: 20px;
  @media screen and (min-width: 800px) {
    .top_right {
      padding: 20px 0;
      box-sizing: border-box;
      height: 200px;
      // margin-top: 20px;
      border-radius: 8px;
      background-color: white;
      display: flex;
      align-items: center;
      justify-content: space-around;
      .right_content {
        border-right: 1px solid #ebeff3;
        &:last-of-type {
          border-right: none;
        }
        height: 100%;
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        img {
          width: 60px;
          height: 60px;
        }
        .line_text {
          font-size: 18px;
          color: #333333;
          font-weight: bold;
          margin-bottom: 10px;
        }
        .member_detail {
          cursor: pointer;
          color: #0075ff;
          text-decoration: underline;
        }
        .text_right {
          // margin-top: 20px;
          font-size: 18px;
          font-family: PingFang SC;
          font-weight: bold;
          color: #333333;
        }
        .num_right {
          font-size: 30px;
          font-family: DIN Condensed;
          font-weight: bold;
          color: #475769;
          .unit {
            font-size: 15px;
          }
        }
      }
    }
    .head_time {
      // border-top: 1px solid #f8f9fa;
      // background-color: white;
      height: 40px;
      line-height: 40px;
      font-size: 16px;
      font-weight: 400;
      color: #666666;
      padding-left: 20px;
      box-sizing: border-box;
    }
    .content_box {
      margin-top: -20px;
      display: flex;
      flex-wrap: wrap;
      .card_box {
        width: 32.5%;
        height: 152px;
        margin-top: 20px;
        margin-right: 1.2%;
        &:nth-of-type(3n) {
          margin-right: 0;
        }
        background: #ffffff;
        box-shadow: 0px 0px 4px 0px rgba(0, 0, 0, 0.08);
        border-radius: 8px;
        padding: 20px;
        .card_title {
          font-size: 18px;
          font-family: Adobe Heiti Std;
          font-weight: normal;
          color: #666666;
          display: flex;
          align-items: center;
          &::before {
            content: "";
            display: inline-block;
            width: 3px;
            height: 20px;
            background: #0075ff;
            margin-right: 5px;
          }
        }
        .card_text {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-top: 30px;
          .num {
            font-size: 30px;
            font-family: DIN Condensed;
            font-weight: bold;
            color: #475769;
          }
          .unit {
            font-size: 17px;
            font-family: Adobe Heiti Std;
            font-weight: normal;
            color: #666666;
          }
          img {
            width: 40px;
            height: 40px;
          }
        }
      }
    }
  }
  @media screen and (max-width: 800px) {
    .top_right {
      padding: 20px 0;
      box-sizing: border-box;
      border-radius: 8px;
      text-align: center;
      background-color: white;
      .right_content {
        padding: 20px;
        border-bottom: 1px solid #ebeff3;
        &:last-of-type {
          border-bottom: none;
        }
        .line_text {
          font-size: 16px;
          color: #333333;
          font-weight: bold;
          margin-bottom: 10px;
        }
        .member_detail {
          cursor: pointer;
          color: #0075ff;
          text-decoration: underline;
        }
        .text_right {
          // margin-top: 20px;
          font-size: 18px;
          font-family: PingFang SC;
          font-weight: bold;
          color: #333333;
        }
        .num_right {
          font-size: 30px;
          font-family: DIN Condensed;
          font-weight: bold;
          color: #475769;
          .unit {
            font-size: 15px;
          }
        }
      }
    }
    .head_time {
      // border-top: 1px solid #f8f9fa;
      // background-color: white;
      height: 40px;
      line-height: 40px;
      font-size: 16px;
      font-weight: 400;
      color: #666666;
      padding-left: 20px;
      box-sizing: border-box;
    }
    .content_box {
      margin-top: -20px;
      .card_box {
        width: 100%;
        height: 152px;
        margin-top: 20px;
        margin-right: 1.2%;
        &:nth-of-type(3n) {
          margin-right: 0;
        }
        background: #ffffff;
        box-shadow: 0px 0px 4px 0px rgba(0, 0, 0, 0.08);
        border-radius: 8px;
        padding: 20px;
        .card_title {
          font-size: 18px;
          font-family: Adobe Heiti Std;
          font-weight: normal;
          color: #666666;
          display: flex;
          align-items: center;
          &::before {
            content: "";
            display: inline-block;
            width: 3px;
            height: 20px;
            background: #0075ff;
            margin-right: 5px;
          }
        }
        .card_text {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-top: 30px;
          .num {
            font-size: 30px;
            font-family: DIN Condensed;
            font-weight: bold;
            color: #475769;
          }
          .unit {
            font-size: 17px;
            font-family: Adobe Heiti Std;
            font-weight: normal;
            color: #666666;
          }
          img {
            width: 40px;
            height: 40px;
          }
        }
      }
    }
  }
}
</style>

