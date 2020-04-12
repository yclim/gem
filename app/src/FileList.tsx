import {
  Cell,
  Column,
  IRegion,
  SelectionModes,
  Table
} from "@blueprintjs/table";
import { Card, Elevation, Tab, TabId, Tabs } from "@blueprintjs/core";
import React, { FunctionComponent, useEffect, useState } from "react";
import { Group, GroupFiles, File } from "./api";
import ruleService from "./api/mock";

interface IProps {
  files: File[];
  setFiles: (fs: File[]) => void;
}

const FileList: FunctionComponent<IProps> = ({ files, setFiles }) => {
  const [activeTab, setActiveTab] = useState("raw");
  const [currentFile, setCurrentFile] = useState<File | null>(null);

  const cellRenderer = (rowIndex: number) => {
    return <Cell>{`${files[rowIndex].fileName}`}</Cell>;
  };

  function handleNavbarTabChange(navbarTabId: TabId) {
    setActiveTab(navbarTabId.toString());
  }

  function handleSection(region: IRegion[]) {
    let reg = region[0];
    if (reg && reg.rows) {
      let r = reg.rows[0];
      setCurrentFile(files[r]);
    }
  }

  function renderRawText() {
    if (currentFile) {
      return (
        <div>
          <table className="bp3-html-table bp3-html-table-striped keyval-table">
            <thead></thead>
            <tbody>
              <tr>
                <td>Filename</td>
                <td> {currentFile?.fileName}</td>
              </tr>
              <tr>
                <td>File size (bytes) </td>
                <td> {currentFile?.fileSize}</td>
              </tr>
              <tr>
                <td>Mime Type</td>
                <td>{currentFile?.mimeType}</td>
              </tr>
            </tbody>
          </table>
          <br />
          <Card elevation={Elevation.ZERO}>
            <span> RAW TEXT </span>
            <pre>{currentFile?.rawText}</pre>
          </Card>
        </div>
      );
    } else {
      return <div> </div>;
    }
  }

  return (
    <div className="files-section">
      <div className="file-list">
        <Table
          numRows={files.length}
          defaultColumnWidth={320}
          selectionModes={SelectionModes.ROWS_AND_CELLS}
          onSelection={handleSection}
        >
          <Column name="Matched filenames" cellRenderer={cellRenderer} />
        </Table>
      </div>
      <div className="file-details">
        <Tabs
          id="TabsExample"
          selectedTabId={activeTab}
          onChange={handleNavbarTabChange}
        >
          <Tab id="raw" title="File Details" panel={renderRawText()} />
          <Tab
            id="meta"
            title="Tika Metadata"
            panel={
              <div>
                <table className="bp3-html-table bp3-html-table-striped keyval-table">
                  <thead>
                    <tr>
                      <th>Metadata Key</th>
                      <th>Metadata Value</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>Resolution Units</td>
                      <td>inch</td>
                    </tr>
                    <tr>
                      <td>Compression Type</td>
                      <td>8 bits</td>
                    </tr>
                    <tr>
                      <td>Data Precision</td>
                      <td>Composable charting library built on top of D3</td>
                    </tr>
                    <tr>
                      <td>Number of Components</td>
                      <td>3</td>
                    </tr>
                    <tr>
                      <td>tiff:ImageLength: </td>
                      <td>3000</td>
                    </tr>
                    <tr>
                      <td>tiff:ImageWidth</td>
                      <td>4000</td>
                    </tr>
                    <tr>
                      <td>X Resolution</td>
                      <td>300 dots</td>
                    </tr>
                    <tr>
                      <td>Y Resolution</td>
                      <td>300 dots</td>
                    </tr>
                    <tr>
                      <td>Original Transmission Reference</td>
                      <td>
                        53616c7465645f5f2368da84ca932841b336ac1a49edb1a93fae938b8db2cb3ec9cc4dc28d7383f1
                      </td>
                    </tr>
                    <tr>
                      <td>Image Width</td>
                      <td>4000 pixels</td>
                    </tr>
                    <tr>
                      <td>IPTC-NAA record</td>
                      <td>92 bytes binary data</td>
                    </tr>
                    <tr>
                      <td>Content-Type</td>
                      <td>image/jpeg</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            }
            panelClassName="ember-panel"
          />
          <Tab
            id="tika"
            title="Tika Content"
            panel={
              <Card elevation={Elevation.ZERO}>
                <pre>{currentFile?.rawText}</pre>
              </Card>
            }
          />
          <Tab
            id="csv"
            title="CSV Details"
            panel={
              <table className="bp3-html-table bp3-html-table-striped keyval-table">
                <tbody>
                  <tr>
                    <td>Header Column Count</td>
                    <td>17</td>
                  </tr>
                  <tr>
                    <td>Header values</td>
                    <td>Timestamp, Name, Address</td>
                  </tr>
                </tbody>
              </table>
            }
          />
          <Tab
            id="xls"
            title="EXCEL Details"
            panel={
              <table className="bp3-html-table bp3-html-table-striped keyval-table">
                <tbody>
                  <tr>
                    <td>Header Column Count</td>
                    <td>17</td>
                  </tr>
                  <tr>
                    <td>Header values</td>
                    <td>Timestamp, Name, Address</td>
                  </tr>
                  <tr>
                    <td>Sheets count</td>
                    <td>3</td>
                  </tr>
                  <tr>
                    <td>Sheets values</td>
                    <td>sheet1, sheet2, sheet3</td>
                  </tr>
                </tbody>
              </table>
            }
          />
          <Tabs.Expander />
        </Tabs>
      </div>
    </div>
  );
};

export default FileList;
