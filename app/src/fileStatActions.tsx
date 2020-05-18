import { Dispatch } from "react";
import groupRuleService from "./api/GroupRuleService";
import { AxiosResponse } from "axios";
import { FileStatDispatchType, GET_FILE_STAT } from "./fileStatReducer";

export interface FileStatAction {
  initFileStat: () => void;
}

export function useFileStatActions(
  state: number[],
  dispatch: Dispatch<FileStatDispatchType>
): FileStatAction {
  return {
    initFileStat: () => {
      console.log("initFileStat");
      groupRuleService.getFileStat().then((resp: AxiosResponse<number[]>) => {
        console.log(resp.data);
        dispatch({ type: GET_FILE_STAT, fileStat: resp.data });
      });
    }
  };
}
