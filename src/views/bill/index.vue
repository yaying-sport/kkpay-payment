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
      <el-form-item label="钱包地址" prop="walletAddress">
        <el-input
          v-model="queryParams.walletAddress"
          placeholder="请输入钱包地址"
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
            v-for="dict in dict.type.payment_bill_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
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
      <el-table-column prop="billType" label="类型" align="center">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.payment_bill_type"
            :value="scope.row.billType"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="交易对方钱包地址"
        align="center"
        prop="walletAddress"
      />
      <el-table-column label="原金额" align="center" prop="amountOriginal" />
      <el-table-column label="账单金额" align="center" prop="amountOrder">
        <template slot-scope="scope">
          <RedGreen
            :redList="[1, 3]"
            :greenList="[2, 4]"
            :needIcon="false"
            :value="scope.row.amountOrder"
            :type="Number(scope.row.billType)"
          ></RedGreen>
        </template>
      </el-table-column>
      <el-table-column label="余额" align="center" prop="amount" />
      <el-table-column label="时间" align="center" prop="createTime">
        <template slot-scope="scope">
          {{ scope.row.createTime && scope.row.createTime.replace("+", " ") }}
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
  </div>
</template>

<script>
import { billList, billTotal } from "@/api/bill";

export default {
  name: "Bill",
  dicts: ["payment_bill_type"],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 日期范围
      dateRange: [],
      // 字典表格数据
      typeList: [],
      queryParams: {
        pageDomain: {
          pageNum: 1,
          pageSize: 10,
        },
        // proxy: "",
        orderNo: "",
        walletAddress: "",
        billType: "",
      },
    };
  },
  created() {
    let startDate = this.moment().format("YYYY-MM-DD") + " 00:00:00";
    let endDate = this.moment().format("YYYY-MM-DD") + " 23:59:59";
    this.dateRange = [startDate, endDate];
    this.getList();
  },
  methods: {
    /** 导出按钮操作 */
    handleExport() {
      if (!this.dateRange || (this.dateRange && this.dateRange.length == 0)) {
        this.$modal.msgWarning("请选择导出时间范围");
        return;
      }
      let params = {
        walletAddress: this.queryParams.walletAddress,
        orderNo: this.queryParams.orderNo,
        billType: this.queryParams.billType,
      };
      this.download(
        "/payment/bill/export",
        this.newDateRange(params, this.dateRange),
        `钱包流水_${new Date().getTime()}.xlsx`
      );
    },
    getSummaries(key, arr) {
      const sums = {};
      key.forEach((column, index) => {
        if (index === 0) {
          sums[column] = `当页总计:`;
          return;
        }
        const values = arr.map((item) => {
          if ([2, 4].includes(item.billType) && column === "amountOrder") {
            return Number("-" + item[column]);
          } else {
            return Number(item[column]);
          }
        });
        // const values = arr.map((item) => Number(item[column]));
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
        walletAddress: this.queryParams.walletAddress,
        orderNo: this.queryParams.orderNo,
        billType: this.queryParams.billType,
      };
      billTotal(this.newDateRange(params, this.dateRange)).then((response) => {
        if (response.data) {
          let obj = response.data;
          key.forEach((column, index) => {
            if (index === 0) {
              sums[column] = `总计:`;
              return;
            } else {
              sums[column] = obj[column];
            }
          });
        }
        this.typeList.push(sums);
      });
    },
    /** 查询字典类型列表 */
    getList() {
      this.loading = true;
      billList(this.newDateRange(this.queryParams, this.dateRange))
        .then((response) => {
          if (response.data) {
            let arr = response.data.rows;
            let key = ["orderNo", "amountOrder"];
            arr.push(this.getSummaries(key, arr));
            this.typeList = arr;
            this.getTotalList(key);
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
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
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