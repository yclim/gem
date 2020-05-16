import React, {
  FunctionComponent,
  useContext,
  useEffect,
  useReducer,
  useState
} from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import "@blueprintjs/table/lib/css/table.css";
import FileList from "./FileList";
import fileStatReducer, { FileStatActions } from "./FileStatReducer";
import { GroupActions } from "./GroupReducer";
import { StoreContext } from "./StoreContext";

export interface UpdateGroupNameInput {
  oldGroupName: string;
  newGroupName: string;
}

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const context = useContext(StoreContext);
  const groups = context.state;
  const dispatcher = context.dispatch;
  if (!dispatcher) throw new Error("illegal dispatcher state");

  const [fileStat, fileStatDispatcher] = useReducer(fileStatReducer, []);

  const [newGroupRuleName, setNewGroupRuleName] = useState<string | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    GroupActions.initGroup(dispatcher);

    FileStatActions.getFileStat(fileStatDispatcher);
  }, []);

  useEffect(() => {
    if (currentGroup != null) {
      groupRuleService.getGroup(currentGroup.name).then(response => {
        // Check for undefined to takecare of deleted group
        if (typeof response.data.matchedFile !== "undefined") {
          setFiles(response.data.matchedFile);
        } else {
          setFiles([]);
        }
      });
    } else {
      setFiles([]);
    }
  }, [currentGroup, groups]);

  return (
    <div className="grid3">
      <RuleList setNewGroupRuleName={setNewGroupRuleName} />
      <GroupList
        currentGroup={currentGroup}
        setCurrentGroup={setCurrentGroup}
        newGroupRuleName={newGroupRuleName}
        fileStatDispatcher={fileStatDispatcher}
        fileStat={fileStat}
      />
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
