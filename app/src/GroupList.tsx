import React, {
  FunctionComponent,
  useContext,
  useEffect,
  useState
} from "react";
import {
  AnchorButton,
  Button,
  Card,
  Elevation,
  FileInput,
  Intent,
  Tag
} from "@blueprintjs/core";
import { File, Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import GroupCard from "./GroupCard";
import { StoreContext } from "./StoreContext";

interface IProps {
  currentGroup: Group | null;
  setCurrentGroup: (group: Group | null) => void;
  newGroupRuleName: string | null;
  setFiles: (fs: File[]) => void;
}

const GroupList: FunctionComponent<IProps> = ({
  currentGroup,
  setCurrentGroup,
  newGroupRuleName,
  setFiles
}) => {
  const [selectedCard, setSelectedCard] = useState<string>("");
  const context = useContext(StoreContext);

  function handleCreateGroup() {
    context.groupsAction?.newGroup();
  }

  function handleNoMatchGroupSelected() {
    setSelectedCard("no_match");
    setCurrentGroup(null);
    setFiles(context.fileStatState.noMatch);
  }
  function handleConflictGroupSelected() {
    setSelectedCard("conflict_match");
    setCurrentGroup(null);
    setFiles(context.fileStatState.conflict);
  }

  function isNoMatchGroup() {
    if (selectedCard === "no_match" && !currentGroup) {
      return true;
    } else {
      return false;
    }
  }

  function isConflictGroup() {
    if (selectedCard === "conflict_match" && !currentGroup) {
      return true;
    } else {
      return false;
    }
  }

  useEffect(() => {
    context.fileStatAction?.initFileStat();
    if (currentGroup != null) {
      groupRuleService.getGroup(currentGroup.name).then(response => {
        // Check for undefined to takecare of deleted group
        if (typeof response.data.matchedFile !== "undefined") {
          setFiles(response.data.matchedFile);
        } else {
          setFiles([]);
        }
      });
    }
  }, [currentGroup, context.groupsState]);

  return (
    <div className="stack">
      <div>
        <Button
          icon="add"
          large={false}
          text="Create Group"
          onClick={() => handleCreateGroup()}
          className="add-right-margin"
        />
      </div>
      <div className="stack">
        {Array.from(context.groupsState, ([k, v]) => v)
          .sort((n1, n2) => {
            if (n1.name > n2.name) {
              return 1;
            }
            if (n1.name < n2.name) {
              return -1;
            }
            return 0;
          })
          .map(g => (
            <GroupCard
              key={g.name}
              group={g}
              focusGroup={currentGroup}
              setFocusGroup={setCurrentGroup}
              newGroupRuleName={newGroupRuleName}
            />
          ))}

        <div>
          <Card
            elevation={isNoMatchGroup() ? Elevation.FOUR : Elevation.ONE}
            className={`group-card ${
              isNoMatchGroup() ? "group-card-focus" : ""
            }`}
            onClick={handleNoMatchGroupSelected}
          >
            <div className="group-card-header">
              <div className="label">No matches</div>
              <div className="counter">
                <Tag
                  round={true}
                  intent={
                    context.fileStatState.noMatch.length > 0
                      ? Intent.WARNING
                      : Intent.SUCCESS
                  }
                >
                  {context.fileStatState.noMatch.length}
                </Tag>
              </div>
            </div>
          </Card>
        </div>

        <div>
          <Card
            elevation={isConflictGroup() ? Elevation.FOUR : Elevation.ONE}
            className={`group-card ${
              isConflictGroup() ? "group-card-focus" : ""
            }`}
            onClick={handleConflictGroupSelected}
          >
            <div className="group-card-header">
              <div className="label">Conflicts</div>
              <div className="counter">
                <Tag
                  round={true}
                  intent={
                    context.fileStatState.conflict.length > 0
                      ? Intent.WARNING
                      : Intent.SUCCESS
                  }
                >
                  {context.fileStatState.conflict.length}
                </Tag>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default GroupList;
