<template>
  <span
    :style="{
      color: redList.includes(type)
        ? 'red'
        : greenList.includes(type)
        ? 'green'
        : '',
    }"
    >{{
      redList.includes(type) && needIcon
        ? "+"
        : greenList.includes(type)
        ? "-"
        : ""
    }}{{ value }}</span
  >
</template>

<script>

export default {
  name: "RedGreen",
  props: {
    value: {
      type: String | Number,
    },
    needIcon:{
      type:Boolean,
      default:true
    },
    type: {
      type: Number,
    },
    redList: {
      default(){
          return []
      },
      type: Array,
    },
    greenList: {
      default(){
          return []
      },
      type: Array,
    },
  },
};
</script>
