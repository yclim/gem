import React from "react";

export const GET_FILE_STAT = "GET_FILE_STAT";

export type FileStatDispatchType = {
  type: typeof GET_FILE_STAT;
  fileStat: number[];
};

export function fileStatReducer(state: number[], action: FileStatDispatchType) {
  switch (action.type) {
    case GET_FILE_STAT: {
      return action.fileStat;
    }
    default:
      throw new Error();
  }
}

export default fileStatReducer;
