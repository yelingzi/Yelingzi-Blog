// 表格信息
export interface TableInfo {
  name: string;
  description: string;
}

// 表格行数据（含UI状态）
export interface TableRow extends TableInfo {
  waitDownload: boolean;
}

// 导出请求
export interface ExportMessage {
  tableName: string;
}

// WebSocket 响应
export interface WsExportResponse {
  status: 'success' | 'error' | 'progress';
  data: {
    url?: string;
    tableName?: string;
  };
}