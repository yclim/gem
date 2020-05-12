import React, {
} from "react";
import groupRuleService from "./api/GroupRuleService";
import { AxiosResponse } from "axios";

const GET_FILE_STAT = "GET_FILE_STAT";

export type FileStatAction =
  { type: typeof GET_FILE_STAT; fileStat: number[]};

export abstract class FileStatActions {
  static getFileStat(dispatcher: React.Dispatch<FileStatAction>) {
    groupRuleService.getFileStat().then((resp: AxiosResponse<number[]>) => {
      dispatcher({type: GET_FILE_STAT, fileStat: resp.data});
    });
  }
}

export function fileStatReducer(state: number[], action: FileStatAction) {
  switch (action.type) {
    case GET_FILE_STAT: {
      return action.fileStat
    }
    default:
      throw new Error();
  }
}

export default fileStatReducer