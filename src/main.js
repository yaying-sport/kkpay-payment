import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import plugins from './plugins' // plugins
import { download } from '@/utils/request'
import moment from "moment";

import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/system/config";
import { parseTime, resetForm, addDateRange, newDateRange, selectDictLabel, selectDictLabels, handleTree, timerFn } from "@/utils/ruoyi";
// 分页组件
import Pagination from "@/components/Pagination";
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar"
// 富文本组件
import Editor from "@/components/Editor"
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 字典标签组件
import DictTag from '@/components/DictTag'
// 头部标签组件
import VueMeta from 'vue-meta'
// 字典数据组件
import DictData from '@/components/DictData'
// 金额颜色组件
import redGreen from '@/components/redGreen'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.newDateRange = newDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.timerFn = timerFn
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
Vue.prototype.moment = moment
Vue.prototype.pickerOptions = {
  shortcuts: [
    {
      text: "今天",
      onClick(picker) {
        let startDate = moment().format("YYYY-MM-DD") + ' 00:00:00';
        let endDate = moment().format("YYYY-MM-DD") + ' 23:59:59';
        picker.$emit("pick", [startDate, endDate]);
      },
    },
    {
      text: "昨天",
      onClick(picker) {
        let startDate = moment().subtract(1, "days").format("YYYY-MM-DD") + ' 00:00:00';
        let endDate = moment().subtract(1, "days").format("YYYY-MM-DD") + ' 23:59:59';
        picker.$emit("pick", [startDate, endDate]);
      },
    },
    {
      text: "本周",
      onClick(picker) {
        let currentWeek = moment().day();
        let end = new Date().valueOf();
        let start = new Date().valueOf() - currentWeek * 24 * 60 * 60 * 1000;
        let startDate = moment(start).format("YYYY-MM-DD") + ' 00:00:00';
        let endDate = moment(end).format("YYYY-MM-DD") + ' 23:59:59';
        picker.$emit("pick", [startDate, endDate]);
      },
    },
    {
      text: "上周",
      onClick(picker) {
        let currentWeek = moment().day();
        let end =
          new Date().valueOf() - (currentWeek + 1) * 24 * 60 * 60 * 1000;
        let start =
          new Date().valueOf() -
          currentWeek * 24 * 60 * 60 * 1000 -
          7 * 24 * 60 * 60 * 1000;
        let startDate = moment(start).format("YYYY-MM-DD") + ' 00:00:00';
        let endDate = moment(end).format("YYYY-MM-DD") + ' 23:59:59';
        picker.$emit("pick", [startDate, endDate]);
      },
    },
    {
      text: "本月",
      onClick(picker) {
        let end = moment(new Date().valueOf()).format("YYYY-MM-DD") + ' 23:59:59';
        let start = `${moment(new Date().valueOf()).format("YYYY-MM")}-1` + ' 00:00:00';
        picker.$emit("pick", [start, end]);
      },
    },
    {
      text: "上月",
      onClick(picker) {
        function getMonthDays(myMonth) {
          var monthStartDate = new Date(moment().year(), myMonth, 1);
          var monthEndDate = new Date(moment().year(), myMonth + 1, 1);
          var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
          return days;
        }
        let lastMonthDate = new Date(); //上月日期
        lastMonthDate.setDate(1);
        lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);
        let lastMonth = lastMonthDate.getMonth();
        let lastMonthStartDate = new Date(moment().year(), lastMonth, 1);
        let lastMonthEndDate = new Date(
          moment().year(),
          lastMonth,
          getMonthDays(lastMonth)
        );
        let start = moment(lastMonthStartDate).format("YYYY-MM-DD") + ' 00:00:00';
        let end = moment(lastMonthEndDate).format("YYYY-MM-DD") + ' 23:59:59';
        picker.$emit("pick", [start, end]);
      },
    },
  ],
},

// 全局组件挂载
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)
Vue.component('RedGreen', redGreen)

Vue.use(directive)
Vue.use(plugins)
Vue.use(VueMeta)
DictData.install()

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
