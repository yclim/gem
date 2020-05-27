import { ExtractConfig, Extractor } from "./api";
import { Dispatch } from "react";
import {
  ExtractConfigDispatchType,
  INIT_EXTRACT_CONFIG,
  UPDATE_COLUMNS,
  UPDATE_EXTRACTOR,
  UPDATE_TABLENAME
} from "./extractConfigReducer";
import extractConfigService from "./api/ExtractConfigService";
import produce from "immer";

export interface ExtractConfigAction {
  init: (groupId: number) => void;
  updateTablename: (groupId: number, tableName: string) => void;
  updateColumns: (columnNames: string[]) => void;
  changeExtractor: (extractor: Extractor) => void;
  updateExcelExtractor: (sheetName: string, columnNames: string[]) => void;
  updateTikaContentRegexExtractor: (regexExp: string) => void;
  updateCSVExtractor: (columnNames: string[]) => void;
}

export function useExtractConfigActions(
  state: ExtractConfig,
  dispatch: Dispatch<ExtractConfigDispatchType>
): ExtractConfigAction {
  return {
    init: groupId => {
      extractConfigService.getExtractConfig(groupId).then(resp =>
        dispatch({
          type: INIT_EXTRACT_CONFIG,
          payload: { extractConfig: resp.data }
        })
      );
    },
    updateTablename: (groupId: number, tableName: string) => {
      const newState = produce(state, draft => {
        draft.tableName = tableName;
      });
      extractConfigService.saveExtractConfig(newState).then(resp => {
        dispatch({
          type: UPDATE_TABLENAME,
          payload: { tableName: resp.data.tableName }
        });
      });
    },
    updateColumns: (columnNames: string[]) => {
      const newState = produce(state, draft => {
        draft.columnNames = [...columnNames];
      });
      extractConfigService.saveExtractConfig(newState).then(resp => {
        dispatch({
          type: UPDATE_COLUMNS,
          payload: { columns: resp.data.columnNames }
        });
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

      extractConfigService.saveExtractConfig(newState).then(resp => {
        if (resp.data.extractor)
          dispatch({
            type: UPDATE_EXTRACTOR,
            payload: { extractor: resp.data.extractor }
          });
      });
    },
    updateTikaContentRegexExtractor: (regexExp: string) => {
      const newState = produce(state, draft => {
        if (draft.extractor) {
          draft.extractor.params[0].value = regexExp;
        }
      });
      extractConfigService.saveExtractConfig(newState).then(resp => {
        if (resp.data.extractor)
          dispatch({
            type: UPDATE_EXTRACTOR,
            payload: { extractor: resp.data.extractor }
          });
      });
    },
    updateCSVExtractor: (columnNames: string[]) => {
      const newState = produce(state, draft => {
        if (draft.extractor) {
          draft.extractor.params[0].value = columnNames.join(",");
        }
      });
      extractConfigService.saveExtractConfig(newState).then(resp => {
        if (resp.data.extractor)
          dispatch({
            type: UPDATE_EXTRACTOR,
            payload: { extractor: resp.data.extractor }
          });
      });
    }
  };
}
