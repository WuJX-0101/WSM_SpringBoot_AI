/**
 * ECharts 按需引入工具
 * 减少打包体积，避免全量引入 1MB 的 echarts
 */
import * as echarts from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  MarkLineComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册所需的组件
echarts.use([
  BarChart,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  MarkLineComponent,
  CanvasRenderer
])

export default echarts
export type { ECharts } from 'echarts/core'
