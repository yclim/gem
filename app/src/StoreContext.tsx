import { ExtractConfig, FileGroupStat, Group } from "./api";
import React, { createContext, FunctionComponent, useReducer } from "react";
import groupsReducer, { GroupDispatchType, initialState } from "./groupReducer";
import { GroupAction, useGroupActions } from "./groupActions";
import fileStatReducer, { fileGroupInitialState } from "./fileStatReducer";
import { FileStatAction, useFileStatActions } from "./fileStatActions";
import extractConfigReducer, {
  extractConfigInitialState
} from "./extractConfigReducer";
import {
  ExtractConfigAction,
  useExtractConfigActions
} from "./extractConfigActions";

interface ContextProps {
  groupsState: Map<string, Group>;
  groupsAction?: GroupAction;
  groupDispatch?: React.Dispatch<GroupDispatchType>;
  fileStatState: FileGroupStat;
  fileStatAction?: FileStatAction;
  extractConfigState: ExtractConfig;
  extractConfigAction?: ExtractConfigAction;
}

const StoreContext = createContext<ContextProps>({
  groupsState: initialState,
  fileStatState: fileGroupInitialState,
  extractConfigState: extractConfigInitialState
});

const StoreProvider: FunctionComponent = ({ children }) => {
  const [groupsState, groupDispatch] = useReducer(groupsReducer, initialState);
  const [fileStatState, fileStatDispatch] = useReducer(
    fileStatReducer,
    fileGroupInitialState
  );
  const [extractConfigState, extractConfigDispatch] = useReducer(
    extractConfigReducer,
    extractConfigInitialState
  );

  const groupsAction = useGroupActions(groupsState, groupDispatch);
  const fileStatAction = useFileStatActions(fileStatState, fileStatDispatch);
  const extractConfigAction = useExtractConfigActions(
    extractConfigState,
    extractConfigDispatch
  );

  return (
    <StoreContext.Provider
      value={{
        groupsState,
        groupsAction,
        groupDispatch,
        fileStatState,
        fileStatAction,
        extractConfigState,
        extractConfigAction
      }}
    >
      {children}
    </StoreContext.Provider>
  );
};

export { StoreContext, StoreProvider };
