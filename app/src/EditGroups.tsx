import React, { FunctionComponent, useEffect, useState } from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group } from "./api";
import ruleService from "./api/mock";
import { Cell, Column, Table } from "@blueprintjs/table";
import "@blueprintjs/table/lib/css/table.css";
import { Tab, TabId, Tabs } from "@blueprintjs/core";
import FileList from "./FileList";

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const [groups, setGroups] = useState(new Map<string, Group>());
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    console.log("getGroups from api");
    if (groups.size === 0) {
      ruleService.getGroups().then((groups: Group[]) => {
        setGroups(new Map(groups.map(g => [g.groupName, g])));
      });
    }
  }, []);

  useEffect(() => {
    if (currentGroup)
      ruleService.getGroupFiles(currentGroup.groupName).then(gfs => {
        setFiles(gfs.files);
      });
  }, [currentGroup]);

  return (
    <div className="horizontal-container">
      <div className="left-nav-section">
        <RuleList groups={groups} setGroups={setGroups} />
      </div>
      <div className="right-main-section">
        <GroupList
          groups={groups}
          setGroups={setGroups}
          currentGroup={currentGroup}
          setCurrentGroup={setCurrentGroup}
        />
      </div>
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
