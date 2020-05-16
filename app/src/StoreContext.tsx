import { Group } from "./api";
import React, { createContext, FunctionComponent, useReducer } from "react";
import groupsReducer, { GroupDispatchType, initialState } from "./GroupReducer";
import { GroupAction, useGroupActions } from "./GroupActions";

interface ContextProps {
  state: Map<string, Group>;
  actions?: GroupAction;
  dispatch?: React.Dispatch<GroupDispatchType>;
}

const StoreContext = createContext<ContextProps>({
  state: initialState
});

const StoreProvider: FunctionComponent = ({ children }) => {
  const [state, dispatch] = useReducer(groupsReducer, initialState);

  const actions = useGroupActions(state, dispatch);

  return (
    <StoreContext.Provider value={{ state, actions, dispatch }}>
      {children}
    </StoreContext.Provider>
  );
};

export { StoreContext, StoreProvider };
