import React, {
  FunctionComponent,
  useContext,
  useEffect,
  useState
} from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group } from "./api";
import "@blueprintjs/table/lib/css/table.css";
import FileList from "./FileList";
import { StoreContext } from "./StoreContext";

export interface UpdateGroupNameInput {
  oldGroupName: string;
  newGroupName: string;
}

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const context = useContext(StoreContext);
  const [newGroupRuleName, setNewGroupRuleName] = useState<string | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    context.groupsAction?.initGroup();
  }, []);

  return (
    <div className="grid3">
      <RuleList setNewGroupRuleName={setNewGroupRuleName} />
      <GroupList
        currentGroup={currentGroup}
        setCurrentGroup={setCurrentGroup}
        newGroupRuleName={newGroupRuleName}
        setFiles={setFiles}
      />
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
