import { ExtractConfig, Extractor, TimestampColumn } from "./api";
import { Dispatch } from "react";
import {
  ADD_TIMESTAMP_COLUMN,
  ExtractConfigDispatchType,
  FILTER_TIMESTAMP_COLUMN_BY_COLUMNS,
  INIT_EXTRACT_CONFIG,
  REMOVE_TIMESTAMP_COLUMN,
  UPDATE_COLUMNS,
  UPDATE_EXTRACTOR,
  UPDATE_TABLENAME
} from "./extractConfigReducer";
import extractConfigService from "./api/ExtractConfigService";
import produce from "immer";

export interface ExtractConfigAction {
  init: (groupId: number) => void;
  updateTablename: (tableName: string) => void;
  updateColumns: (columnNames: string[]) => void;
  changeExtractor: (extractor: Extractor) => void;
  updateExcelExtractor: (sheetName: string, columnNames: string[]) => void;
  updateTikaContentRegexExtractor: (regexExp: string) => void;
  updateCSVExtractor: (columnNames: string[]) => void;
  addTimestampColumn: (timestampColumn: TimestampColumn) => void;
  removeTimestampColumn: (index: number) => void;
  filterTimestampColumns: (columns: string[]) => void;
}

export function useExtractConfigActions(
  state: ExtractConfig,
  dispatch: Dispatch<ExtractConfigDispatchType>
): ExtractConfigAction {
  return {
    init: groupId => {
      extractConfigService.getExtractConfig(groupId).then(resp => {
        if (!resp.data) {
          const config: ExtractConfig = {
            columnNames: [],
            extractor: null,
            groupId,
            tableName: "",
            timestampColumns: []
          };
          dispatch({
            type: INIT_EXTRACT_CONFIG,
            payload: { extractConfig: config }
          });
        } else {
          dispatch({
            type: INIT_EXTRACT_CONFIG,
            payload: { extractConfig: resp.data }
          });
        }
      });
    },
    updateTablename: (tableName: string) => {
      dispatch({
        type: UPDATE_TABLENAME,
        payload: { tableName }
      });
    },
    updateColumns: (columnNames: string[]) => {
      dispatch({
        type: UPDATE_COLUMNS,
        payload: { columns: columnNames }
      });
    },
    changeExtractor: (extractor: Extractor) => {
      dispatch({
        type: UPDATE_EXTRACTOR,
        payload: { extractor }
      });
    },
    updateExcelExtractor: (sheetName: string, columnNames: string[]) => {
      const newState = produce(state, draft => {
        if (draft.extractor) {
          draft.extractor.params[0].value = sheetName;
          draft.extractor.params[1].value = columnNames.join(",");
        }
      });
      if (newState.extractor) {
        dispatch({
          type: UPDATE_EXTRACTOR,
          payload: { extractor: newState.extractor }
        });
      }
    },
    updateTikaContentRegexExtractor: (regexExp: string) => {
      const newState = produce(state, draft => {
        if (draft.extractor) {
          draft.extractor.params[0].value = regexExp;
        }
      });
      if (newState.extractor) {
        dispatch({
          type: UPDATE_EXTRACTOR,
          payload: { extractor: newState.extractor }
        });
      }
    },
    updateCSVExtractor: (columnNames: string[]) => {
      const newState = produce(state, draft => {
        if (draft.extractor) {
          draft.extractor.params[0].value = columnNames.join(",");
        }
      });
      if (newState.extractor) {
        dispatch({
          type: UPDATE_EXTRACTOR,
          payload: { extractor: newState.extractor }
        });
      }
    },
    addTimestampColumn: (timestampColumn: TimestampColumn) => {
      dispatch({
        type: ADD_TIMESTAMP_COLUMN,
        payload: { timestampColumn }
      });
    },
    removeTimestampColumn: (index: number) => {
      dispatch({
        type: REMOVE_TIMESTAMP_COLUMN,
        payload: { index }
      });
    },

    filterTimestampColumns: (columns: string[]) => {
      dispatch({
        type: FILTER_TIMESTAMP_COLUMN_BY_COLUMNS,
        payload: { columns }
      });
    }
  };
}
