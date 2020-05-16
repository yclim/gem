import { Group } from "./api";
import React, { createContext, FunctionComponent, useReducer } from "react";
import groupsReducer, { GroupDispatchType, initialState } from "./GroupReducer";
import { GroupAction, useGroupActions } from "./GroupActions";
import fileStatReducer from "./fileStatReducer";
import { FileStatAction, useFileStatActions } from "./fileStatActions";

interface ContextProps {
  groupsState: Map<string, Group>;
  groupsAction?: GroupAction;
  groupDispatch?: React.Dispatch<GroupDispatchType>;
  fileStatState: number[];
  fileStatAction?: FileStatAction;
}

const StoreContext = createContext<ContextProps>({
  groupsState: initialState,
  fileStatState: []
});

const StoreProvider: FunctionComponent = ({ children }) => {
  const [groupsState, groupDispatch] = useReducer(groupsReducer, initialState);
  const [fileStatState, fileStatDispatch] = useReducer(fileStatReducer, []);

  const groupsAction = useGroupActions(groupsState, groupDispatch);
  const fileStatAction = useFileStatActions(fileStatState, fileStatDispatch);

  return (
    <StoreContext.Provider
      value={{
        groupsState,
        groupsAction,
        groupDispatch,
        fileStatState,
        fileStatAction
      }}
    >
      {children}
    </StoreContext.Provider>
  );
};

export { StoreContext, StoreProvider };
