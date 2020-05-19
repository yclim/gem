import {FileGroup, FileGroupStat, Group} from "./api";
import React, { createContext, FunctionComponent, useReducer } from "react";
import groupsReducer, { GroupDispatchType, initialState } from "./groupReducer";
import { GroupAction, useGroupActions } from "./groupActions";
import fileStatReducer, {fileGroupInitialState} from "./fileStatReducer";
import { FileStatAction, useFileStatActions } from "./fileStatActions";

interface ContextProps {
    groupsState: Map<string, Group>;
    groupsAction?: GroupAction;
    groupDispatch?: React.Dispatch<GroupDispatchType>;
    fileStatState: FileGroupStat;
    fileStatAction?: FileStatAction;
}

const StoreContext = createContext<ContextProps>({
    groupsState: initialState,
    fileStatState: fileGroupInitialState
});

const StoreProvider: FunctionComponent = ({ children }) => {
    const [groupsState, groupDispatch] = useReducer(groupsReducer, initialState);
    const [fileStatState, fileStatDispatch] = useReducer(fileStatReducer, fileGroupInitialState);

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
