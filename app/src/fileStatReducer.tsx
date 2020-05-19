import React from "react";
import { FileGroup, FileGroupStat } from "./api";

export const GET_FILE_STAT = "GET_FILE_STAT";

export type FileStatDispatchType = {
  type: typeof GET_FILE_STAT;
  fileStat: FileGroupStat;
};

export const fileGroupInitialState: Map<string, FileGroup[]> = new Map();

export function fileStatReducer(
  state: FileGroupStat,
  action: FileStatDispatchType
) {
  switch (action.type) {
    case GET_FILE_STAT: {
      return action.fileStat;
    }
    default:
      throw new Error();
  }
}

export default fileStatReducer;
