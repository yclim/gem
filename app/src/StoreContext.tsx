import { Group } from "./api";
import React, {
  createContext,
  FunctionComponent,
  useContext,
  useReducer
} from "react";
import groupsReducer, { GroupAction, initialState } from "./GroupReducer";

interface ContextProps {
  state: Map<string, Group>;
  dispatch?: React.Dispatch<GroupAction>;
}

const StoreContext = createContext<ContextProps>({
  state: initialState
});

const StoreProvider: FunctionComponent = ({ children }) => {
  const [state, dispatch] = useReducer(groupsReducer, initialState);

  return (
    <StoreContext.Provider value={{ state, dispatch }}>
      {children}
    </StoreContext.Provider>
  );
};

export { StoreContext, StoreProvider };
