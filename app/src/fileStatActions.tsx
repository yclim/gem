import { Dispatch } from "react";
import groupRuleService from "./api/GroupRuleService";
import { AxiosResponse } from "axios";
import { FileStatDispatchType, GET_FILE_STAT } from "./fileStatReducer";
import { FileGroupStat } from "./api";

export interface FileStatAction {
  initFileStat: () => void;
}

export function useFileStatActions(
  state: FileGroupStat,
  dispatch: Dispatch<FileStatDispatchType>
): FileStatAction {
  return {
    initFileStat: () => {
      groupRuleService
        .getFileStatMatches()
        .then((resp: AxiosResponse<FileGroupStat>) => {
          console.log(resp.data);
          dispatch({ type: GET_FILE_STAT, fileStat: resp.data });
        });
    }
  };
}
