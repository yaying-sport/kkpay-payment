<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      v-show="showSearch"
      label-width="100px"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会员钱包地址" prop="walletAddress">
        <el-input
          v-model="queryParams.walletAddress"
          placeholder="请输入会员钱包地址"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="billType">
        <el-select
          v-model="queryParams.billType"
          placeholder="请选择类型"
          clearable
          size="small"
        >
          <el-option
            v-for="dict in typeArr"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时间查询">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 215px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          :picker-options="pickerOptions"
          :default-time="['00:00:00', '23:59:59']"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd('手动上分')"
          >手动上分</el-button
        >
        <!-- v-hasPermi="['payment:member:deposit']" -->
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-minus"
          size="mini"
          @click="handleAdd('手动下分')"
          >手动下分</el-button
        >
        <!-- v-hasPermi="['payment:member:deduction']" -->
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="typeList">
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column
        label="会员钱包地址"
        align="center"
        prop="walletAddress"
      />
      <el-table-column prop="billType" label="类型" align="center">
        <template slot-scope="scope">
          <dict-tag :options="typeArr" :value="scope.row.billType" />
        </template>
      </el-table-column>
      <el-table-column label="订单金额" align="center" prop="amount">
        <template slot-scope="scope">
          <RedGreen
            :redList="[3]"
            :greenList="[4]"
            :value="scope.row.amount"
            :type="Number(scope.row.billType)"
          ></RedGreen>
        </template>
      </el-table-column>
      <el-table-column label="账号余额" align="center" prop="balance" />
      <el-table-column label="时间" align="center" prop="createTime">
        <template slot-scope="scope">
          {{scope.row.createTime && scope.row.createTime.replace('+',' ')}}
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageDomain.pageNum"
      :limit.sync="queryParams.pageDomain.pageSize"
      @pagination="getList"
    />
    <!-- 添加或修改参数配置对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="500px"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="会员钱包地址" prop="walletAddress">
          <el-input
            v-model="form.walletAddress"
            placeholder="请输入会员钱包地址"
          />
        </el-form-item>
        <el-form-item
          :label="title == '手动上分' ? '上分金额' : '下分金额'"
          prop="amount"
        >
          <el-input
            type="text"
            autocomplete="new-password"
            v-model="form.amount"
            @input="
              form.amount = form.amount
                .replace(/[^\d^\.]+/g, '')
                .replace('.', '$#$')
                .replace(/\./g, '')
                .replace('$#$', '.')
            "
            clearable
            :placeholder="
              title == '手动上分' ? '请输入上分金额' : '请输入下分金额'
            "
          />
        </el-form-item>
        <el-form-item label="支付密码" prop="payPwd">
          <el-input
            type="password"
            autocomplete="new-password"
            v-model="form.payPwd"
            show-password
            placeholder="请输入支付密码"
          />
        </el-form-item>
        <el-form-item label="google验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入google验证码" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="timerFn(submitForm, 1000, true)()"
          >确 定</el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  memberTradeList,
  punchMemberAmount,
  memberTradeListTotal,
} from "@/api/payment";

export default {
  name: "Payment",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 字典表格数据
      typeList: [],
      // 日期范围
      dateRange: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageDomain: {
          pageNum: 1,
          pageSize: 10,
        },
        orderNo: "",
        billType: "",
        walletAddress: "",
      },
      rules: {
        amount: [{ required: true, message: "金额不能为空", trigger: "blur" }],
        payPwd: [
          { required: true, message: "支付密码不能为空", trigger: "blur" },
        ],
        walletAddress: [
          { required: true, message: "会员钱包地址不能为空", trigger: "blur" },
        ],
      },
      typeArr: [
        {
          label: "会员充币",
          value: '3',
          raw: { listClass: "default" },
        },
        {
          label: "会员扣币",
          value: '4',
          raw: { listClass: "default" },
        },
      ],
    };
  },
  created() {
    let startDate = this.moment().format("YYYY-MM-DD") + " 00:00:00";
    let endDate = this.moment().format("YYYY-MM-DD") + " 23:59:59";
    this.dateRange = [startDate, endDate];
    this.getList();
  },
  methods: {
    getSummaries(key, arr) {
      const sums = {};
      key.forEach((column, index) => {
        if (index === 0) {
          sums[column] = `当页总计:`;
          return;
        }
        const values = arr.map((item) => {
          if ([4].includes(item.billType) && column === "amount") {
            return Number("-" + item[column]);
          } else {
            return Number(item[column]);
          }
        });
        if (!values.every((value) => isNaN(value))) {
          let num = values.reduce((prev, curr) => {
            const value = Number(curr);
            if (!isNaN(value)) {
              return prev + curr;
            } else {
              return prev;
            }
          }, 0);
          if (String(num).indexOf(".") > -1) {
            sums[column] = parseFloat(num.toFixed(3));
          } else {
            sums[column] = num;
          }
        } else {
          sums[column] = "";
        }
      });
      return sums;
    },
    getTotalList(key) {
      const sums = {};
      let params = {
        orderNo: this.queryParams.orderNo,
        billType: this.queryParams.billType,
        walletAddress: this.queryParams.walletAddress,
      };
      memberTradeListTotal(this.newDateRange(params, this.dateRange)).then(
        (response) => {
          if (response.data) {
            let obj = response.data;
            key.forEach((column, index) => {
              if (index === 0) {
                sums[column.key] = `总计:`;
                return;
              } else {
                sums[column.key] = obj[column.value];
              }
            });
          }
          this.typeList.push(sums);
        }
      );
    },
    /** 查询字典类型列表 */
    getList() {
      this.loading = true;
      memberTradeList(this.newDateRange(this.queryParams, this.dateRange))
        .then((response) => {
          if (response.data) {
            let arr = response.data.rows;
            let key = ["orderNo", "amount"];
            arr.push(this.getSummaries(key, arr));
            this.typeList = arr;
            console.log('arr :>> ', arr);
            let key1 = [
              { key: "orderNo", value: "" },
              { key: "amount", value: "amount" },
            ];
            this.getTotalList(key1);
            this.total = response.data.total;
          }
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageDomain.pageNum = 1;
      this.getList();
    },
    /** 导出按钮操作 */
    handleExport() {
      if (!this.dateRange || (this.dateRange && this.dateRange.length == 0)) {
        this.$modal.msgWarning("请选择导出时间范围");
        return;
      }
      let params = {
        orderNo: this.queryParams.orderNo,
        billType: this.queryParams.billType,
        walletAddress: this.queryParams.walletAddress,
      };
      this.download(
        "/payment/trade/export",
        this.newDateRange(params, this.dateRange),
        `会员上下分_${new Date().getTime()}.xlsx`
      );
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 手动创建提现单 */
    handleAdd(title) {
      this.reset();
      if (title == "手动上分") {
        this.form.billType = 3;
      } else {
        this.form.billType = 4;
      }
      this.open = true;
      this.title = title;
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          punchMemberAmount(this.form).then((response) => {
            this.$modal.msgSuccess("操作成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        amount: "",
        code: "",
        payPwd: "",
        walletAddress: "",
        remark: "",
        billType: "",
      };
      this.resetForm("form");
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-table__body-wrapper .el-table__body tbody {
  .el-table__row:last-of-type {
    background-color: #f8f8f9;
    font-size: 13px;
    font-weight: 600;
  }
  .el-table__row:nth-last-of-type(2) {
    background-color: #f8f8f9;
    font-size: 13px;
    font-weight: 600;
  }
}
</style>