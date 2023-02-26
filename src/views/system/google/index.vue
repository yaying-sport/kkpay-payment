<template>
  <div class="google_box">
    <el-steps
      :active="activeIndex"
      finish-status="success"
      simple
      style="margin-top: 20px"
    >
      <el-step title="1:下载谷歌验证器"></el-step>
      <el-step title="2:设置密钥"></el-step>
      <el-step title="3:验证密钥"></el-step>
      <el-step title="4:启动谷歌验证"></el-step>
    </el-steps>
    <div class="content_box" v-show="activeIndex == 0">
      <div class="qrcode" id="qrcode"></div>
      <!-- <div class="red_text">备注:请尽量使用手机扫码</div> -->
      <div class="red_text mt20">该二维码为谷歌验证器的下载地址，如未下载，请先扫码下载谷歌验证器app,若已下载请点击“下一步”</div>
    </div>
    <div class="content_box" v-show="activeIndex == 1">
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="账号" prop="username">
          <el-input
            type="text"
            style="width: 300px"
            v-model="form.username"
            readonly
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item label="密钥" prop="googleAuthKey">
          <el-input
            type="text"
            style="width: 300px"
            readonly
            v-model="form.googleAuthKey"
            placeholder="请输入密钥"
          />
        </el-form-item>
        <el-form-item label="密钥二维码" >
          <div class="keyqrcode" id="keyqrcode"></div>
        </el-form-item>
      </el-form>
    </div>
    <div class="content_box" v-show="activeIndex == 2">
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="验证码" prop="validcode">
          <div class="valid_box">
            <el-input
              type="text"
              readonly
              v-model="form.validcode"
              placeholder="请输入验证码"
            />
            <el-button type="primary" @click="refresh">刷新</el-button>
          </div>
        </el-form-item>
        <div>
          请比对此验证码是否和手机app上生成的验证码一致，如果一致，请进行下一步，否在，返回上一步重新设置<span
            style="color: red"
            >(验证码30秒更新一次)</span
          >
        </div>
      </el-form>
    </div>
    <div class="content_box" v-show="activeIndex == 3">
      <div class="red_text">
        {{ alStart ? "已启用谷歌验证" : "点击启用按钮立即启用谷歌验证" }}
      </div>
    </div>
    <div class="btn_box">
      <el-button type="primary" v-if="activeIndex != 0" @click="back"
        >上一步</el-button
      >
      <el-button type="primary" v-if="activeIndex != 3" @click="next"
        >下一步</el-button
      >
      <el-button
        type="primary"
        v-if="activeIndex == 3 && !alStart"
        @click="start"
        >启用</el-button
      >
    </div>
  </div>
</template>
<script>
import {
  getCurrentUserGoogleAuthKey,
  getGoogleAuthKey,
  getGoogleCode,
  saveUserGoogleKey,
} from "@/api/system/google";
import QRCode from "qrcodejs2";
export default {
  data() {
    return {
      activeIndex: 0,
      form: {
        googleAuthKey: "",
        validcode: "",
        username: "",
      },
      keyqrcode: null,
      // 表单校验
      rules: {
        username: [
          { required: true, message: "账号不能为空", trigger: "blur" },
        ],
        googleAuthKey: [
          {
            required: true,
            message: "密钥不能为空",
            trigger: "blur",
          },
        ],
        validcode: [
          {
            required: true,
            message: "验证码不能为空",
            trigger: "blur",
          },
        ],
      },
      googleObj: {},
      alStart: false,
    };
  },
  methods: {
    qrcode() {
      //生成二维码
      let qrcode = new QRCode("qrcode", {
        width: 300,
        height: 300,
        text: this.googleObj.googleAuthDownloadUrl, // 二维码内容
        render: "canvas", // 设置渲染方式（有两种方式 table和canvas，默认是canvas）
        background: "#f0f", // 背景色
        foreground: "#ff0", // 前景色
      });
      let myCanvas = document.querySelector("canvas");
      let img = myCanvas.toDataURL("image/png");
      this.qImg = img;
    },
    qrcodekey() {
      //生成二维码
      if(this.keyqrcode){
        this.keyqrcode.makeCode(`otpauth://totp/${this.form.username}?secret=${this.form.googleAuthKey}&issuer=jjpay_agent`)
        return
      }
      this.keyqrcode = new QRCode("keyqrcode", {
        width:100,
        height:100,
        text: `otpauth://totp/${this.form.username}?secret=${this.form.googleAuthKey}&issuer=jjpay_agent`, // 二维码内容
        render: "canvas", // 设置渲染方式（有两种方式 table和canvas，默认是canvas）
        background: "#f0f", // 背景色
        foreground: "#ff0", // 前景色
      });
      let myCanvas = document.querySelector("canvas");
      let img = myCanvas.toDataURL("image/png");
      this.qImg = img;
    },
    next() {
      if (this.activeIndex < 3) {
        this.activeIndex++;
        if (this.activeIndex == 1) {
          this.getGoogleAuthKey();
        }
        if (this.activeIndex == 2) {
          this.getGoogleCode();
        }
      }
    },
    back() {
      if (this.activeIndex < 5 && this.activeIndex > 0) {
        this.activeIndex--;
        if (this.activeIndex == 1) {
          this.getGoogleAuthKey();
        }
        if (this.activeIndex == 2) {
          this.getGoogleCode();
        }
      }
    },
    refresh() {
      this.getGoogleCode();
    },
    start() {
      saveUserGoogleKey({ googleAuthKey: this.form.googleAuthKey }).then(
        (res) => {
          this.alStart = true;
        }
      );
    },
    getGoogleAuthKey() {
      getGoogleAuthKey().then((res) => {
        this.form.username = res.data.username;
        this.form.googleAuthKey = res.data.googleAuthKey;
        this.$nextTick(()=>{
          this.qrcodekey()
        })
      });
    },
    getGoogleCode() {
      getGoogleCode({ googleAuthKey: this.form.googleAuthKey }).then((res) => {
        this.form.validcode = res.data.code;
      });
    },
    getCodeImg() {
      getCurrentUserGoogleAuthKey().then((res) => {
        this.googleObj = res.data;
        this.$nextTick(()=>{
          this.qrcode();
        })
        if (this.googleObj.googleAuthKey) {
          this.alStart = true;
        }
      });
    },
  },
  mounted() {
    this.getCodeImg();
  },
};
</script>
<style lang="scss" scoped>
.google_box {
  .content_box {
    height: 330px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 60px;
    text-align: center;
    .red_text {
      color: red;
      font-size: 20px;
    }
    .valid_box {
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 10px;
      box-sizing: border-box;
      ::v-deep .el-input__inner {
        border-radius: 4px 0 0 4px;
      }
      ::v-deep .el-button {
        height: 36px;
        border-radius: 0 4px 4px 0;
      }
    }
  }
  .btn_box {
    margin-top: 60px;
    text-align: center;
  }
}
</style>