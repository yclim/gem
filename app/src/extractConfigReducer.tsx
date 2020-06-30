import { ExtractConfig, Extractor, TimestampColumn } from "./api";
import produce from "immer";

export const INIT_EXTRACT_CONFIG = "INIT_EXTRACT_CONFIG";
export const UPDATE_TABLENAME = "UPDATE_TABLENAME";
export const UPDATE_COLUMNS = "UPDATE_COLUMNS";
export const UPDATE_EXTRACTOR = "UPDATE_EXTRACTOR";
export const ADD_TIMESTAMP_COLUMN = "ADD_TIMESTAMP_COLUMN";
export const REMOVE_TIMESTAMP_COLUMN = "REMOVE_TIMESTAMP_COLUMN";
export const FILTER_TIMESTAMP_COLUMN_BY_COLUMNS =
  "FILTER_TIMESTAMP_COLUMN_BY_COLUMNS";

export type ExtractConfigDispatchType =
  | {
      type: typeof INIT_EXTRACT_CONFIG;
      payload: { extractConfig: ExtractConfig };
    }
  | {
      type: typeof UPDATE_TABLENAME;
      payload: { tableName: string };
    }
  | {
      type: typeof UPDATE_COLUMNS;
      payload: { columns: string[] };
    }
  | {
      type: typeof UPDATE_EXTRACTOR;
      payload: { extractor: Extractor };
    }
  | {
      type: typeof ADD_TIMESTAMP_COLUMN;
      payload: { timestampColumn: TimestampColumn };
    }
  | {
      type: typeof REMOVE_TIMESTAMP_COLUMN;
      payload: { index: number };
    }
  | {
      type: typeof FILTER_TIMESTAMP_COLUMN_BY_COLUMNS;
      payload: { columns: string[] };
    };
export const extractConfigInitialState: ExtractConfig = {
  columnNames: [],
  extractor: null,
  groupId: 0,
  tableName: "",
  timestampColumns: []
};

// hardcoded list the extractors
export const CSV_EXTRACTOR = "innohack.gem.core.extract.CSVExtractor";
export const EXCEL_EXTRACTOR = "innohack.gem.core.extract.ExcelExtractor";
export const TIKA_CONTENT_EXTRACTOR =
  "innohack.gem.core.extract.TikaContentExtractor";

export default function extractConfigReducer(
  state: ExtractConfig,
  action: ExtractConfigDispatchType
) {
  switch (action.type) {
    case INIT_EXTRACT_CONFIG:
      return produce(action.payload.extractConfig, draft => {
        // no change
      });
    case UPDATE_TABLENAME:
      return produce(state, draft => {
        draft.tableName = action.payload.tableName;
      });
    case UPDATE_COLUMNS:
      return produce(state, draft => {
        draft.columnNames = action.payload.columns;
      });
    case UPDATE_EXTRACTOR:
      return produce(state, draft => {
        draft.extractor = { ...action.payload.extractor };
      });
    case ADD_TIMESTAMP_COLUMN:
      return produce(state, draft => {
        draft.timestampColumns = [
          ...draft.timestampColumns,
          action.payload.timestampColumn
        ];
      });
    case REMOVE_TIMESTAMP_COLUMN:
      return produce(state, draft => {
        draft.timestampColumns.splice(action.payload.index, 1);
      });
    case FILTER_TIMESTAMP_COLUMN_BY_COLUMNS:
      return produce(state, draft => {
        draft.timestampColumns = draft.timestampColumns.filter(t =>
          action.payload.columns.includes(t.fromColumn)
        );
      });
    default:
      throw new Error();
  }
}
