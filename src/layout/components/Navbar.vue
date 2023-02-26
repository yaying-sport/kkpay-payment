<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb
      id="breadcrumb-container"
      class="breadcrumb-container"
      v-if="!topNav"
    />
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav" />
    <div class="right-menu">
      <!-- <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />
        
        <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

      </template> -->
      <!-- <div
        style="cursor: pointer; font-size: 15px;color:red;"
        class="right-menu-item gray_line"
        @click="handleEdit('deposit', '商户存款使用指导')"
      >
        商户存款教程
      </div>
      <div style="cursor:pointer;font-size:16px;" class="right-menu-item" @click="goDealwith">玩家APP充值待处理: <span style="color:rgb(64, 158, 255);">{{$store.getters.notDealwithNum}}</span></div>
      <audio ref="app_mp3" id="app_mp3" src="../../assets/mp3/app.mp3" hidden></audio> -->
      <el-dropdown 
        class="avatar-container right-menu-item hover-effect"
        trigger="click"
      >
        <div class="avatar-wrapper">
          <img src="../../assets/images/profile.jpg" class="user-avatar" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <!-- <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link> -->
          <!-- <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item> -->
          <el-dropdown-item
            @click.native="handleEdit('login', '修改登录密码')"
          >
            <span>修改登录密码</span>
          </el-dropdown-item>
          <el-dropdown-item
            divided
            @click.native="handleEdit('pay', '修改支付密码')"
          >
            <span>修改支付密码</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" :width="dialogType == 'deposit' ? '900px' : '600px'" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <template v-if="dialogType == 'login'">
          <el-form-item label="登录名称">
            <el-input
              type="text"
              v-model="name"
              readonly
              style="width: 400px"
              placeholder="请输入登录名称"
            />
          </el-form-item>
          <el-form-item label="旧登录密码" prop="originalPwd">
            <el-input
              type="password"
              autocomplete="new-password"
              style="width: 400px"
              show-password
              v-model="form.originalPwd"
              placeholder="请输入旧登录密码"
            />
          </el-form-item>
          <el-form-item label="新登录密码" prop="newPwd">
            <el-input
              type="password"
              style="width: 400px"
              show-password
              v-model="form.newPwd"
              placeholder="请输入新登录密码"
            />
          </el-form-item>
          <el-form-item label="再次确认新密码" prop="confirmPwd">
            <el-input
              type="password"
              style="width: 400px"
              show-password
              v-model="form.confirmPwd"
              placeholder="请再次确认新密码"
            />
          </el-form-item>
          <el-form-item label="谷歌验证码">
            <el-input
              type="text"
              style="width: 400px"
              v-model="form.code"
              placeholder="请输入谷歌验证码"
            />
          </el-form-item>
        </template>
        <template v-if="dialogType == 'pay'">
          <el-form-item label="登录名称" prop="realName">
            <el-input
              type="text"
              v-model="name"
              readonly
              style="width: 400px"
              placeholder="请输入登录名称"
            />
          </el-form-item>
          <el-form-item label="旧支付密码" prop="originalPwd">
            <el-input
              type="password"
              style="width: 400px"
              show-password
              v-model="form.originalPwd"
              placeholder="请输入旧支付密码"
            />
          </el-form-item>
          <el-form-item label="新支付密码" prop="newPwd">
            <el-input
              type="password"
              style="width: 400px"
              show-password
              v-model="form.newPwd"
              placeholder="请输入新支付密码"
            />
          </el-form-item>
          <el-form-item label="再次确认新密码" prop="confirmPwd">
            <el-input
              type="password"
              style="width: 400px"
              show-password
              v-model="form.confirmPwd"
              placeholder="请再次确认新密码"
            />
          </el-form-item>
          <el-form-item label="谷歌验证码">
            <el-input
              type="text"
              style="width: 400px"
              v-model="form.code"
              placeholder="请输入谷歌验证码"
            />
          </el-form-item>
        </template>
        <template v-if="dialogType == 'deposit'">
          <div>
            <img width="860px" src="@/assets/images/merchant_deposit.jpg" alt="">
          </div>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" v-if="dialogType != 'deposit'" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import RuoYiGit from "@/components/RuoYi/Git";
import RuoYiDoc from "@/components/RuoYi/Doc";
import { editLoginPwd, editPayPwd } from "@/api/system/google";
export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc,
  },
  data() {
    let validateConfirmPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请确认密码"));
      } else {
        if (this.form.confirmPwd != this.form.newPwd) {
          callback(new Error("两次密码不一致"));
        }
        callback();
      }
    };
    return {
      dialogType: "",
      //弹窗标题
      title: "",
      //弹窗开关
      open: false,
      // 表单参数
      form: {},
      dealwithNum:0,
      timer:null,
      // 表单校验
      rules: {
        originalPwd: [
          { required: true, message: "原密码不能为空", trigger: "blur" },
        ],
        newPwd: [
          {
            required: true,
            message: "新密码不能为空",
            trigger: "blur",
          },
        ],
        confirmPwd: [
          { required: true, validator: validateConfirmPass, trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    ...mapGetters(["sidebar", "avatar", "device", "name"]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings;
      },
      set(val) {
        this.$store.dispatch("settings/changeSetting", {
          key: "showSettings",
          value: val,
        });
      },
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav;
      },
    },
  },
  watch:{
    '$store.getters.notDealwithNum': {
      handler: function (val,old) {
        if((old || old == 0) && val > old){
          this.$refs.app_mp3.play()
        }
      },
      deep: true,
    },
  },
  created(){
    // this.getUnDealwith()
    // clearInterval(this.timer)
    // this.timer = setInterval(this.getUnDealwith,10000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
    this.timer = null
  },
  methods: {
    goDealwith(){ 
      if(this.$route.name == 'AuditDeposit'){
        return
      }
      this.$router.push('/auditDeposit')
    },
    getUnDealwith(){
      this.$store.dispatch('GetUnDealwith').then((res)=>{})
    },
    handleEdit(type, title, row) {
      this.currentRow = row;
      this.dialogType = type;
      this.reset();
      this.open = true;
      this.title = title;
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        originalPwd: "",
        newPwd: "",
        confirmPwd: "",
        code: "",
      };
      this.resetForm("form");
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.dialogType == "login") {
            let params = {
              originalPwd: this.form.originalPwd,
              newPwd: this.form.newPwd,
              confirmPwd: this.form.confirmPwd,
              code: this.form.code,
            };
            editLoginPwd(params).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          }
          if (this.dialogType == "pay") {
            let params = {
              originalPwd: this.form.originalPwd,
              newPwd: this.form.newPwd,
              confirmPwd: this.form.confirmPwd,
              code: this.form.code,
            };
            editPayPwd(params).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    async logout() {
      this.$confirm("确定注销并退出系统吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$store.dispatch("LogOut").then(() => {
            location.href = "/index";
          });
        })
        .catch(() => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }
    .gray_line {
      position: relative;
      &::after {
        display: inline-block;
        content: "";
        width: 1px;
        height: 16px;
        background-color: #dcdfe6;
        position: absolute;
        top: 16px;
        right: 0;
      }
    }
    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
