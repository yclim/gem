import {
  Cell,
  Column,
  IRegion,
  SelectionModes,
  Table
} from "@blueprintjs/table";
import { Blockquote, Tab, TabId, Tabs } from "@blueprintjs/core";
import React, { FunctionComponent, useState } from "react";
import { CsvFeature, File } from "./api";
import fileService from "./api/FileService";

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
    const reg = region[0];
    if (reg && reg.rows) {
      const r = reg.rows[0];
      fileService.getFile(files[r].fileName, files[r].directory).then(f => {
        setCurrentFile(f.data);
      });
    }
  }

  function renderFileDetailTab() {
    if (currentFile) {
      return (
        <div>
          <table className="bp3-html-table bp3-html-table-striped keyval-table">
            <thead />
            <tbody>
              <tr>
                <td>Filename</td>
                <td> {currentFile?.fileName}</td>
              </tr>
              <tr>
                <td>Directory</td>
                <td>{currentFile?.directory}</td>
              </tr>
              <tr>
                <td>File size (bytes) </td>
                <td> {currentFile?.size}</td>
              </tr>
              <tr>
                <td>Extension</td>
                <td>{currentFile?.extension}</td>
              </tr>
              <tr>
                <td>Mime Type</td>
                <td>{currentFile?.mimeType}</td>
              </tr>
            </tbody>
          </table>
        </div>
      );
    } else {
      return <div />;
    }
  }

  function renderMetadata(meta: Map<string, string>) {
    return (
      <table className="bp3-html-table bp3-html-table-striped keyval-table">
        <thead>
          <tr>
            <th>Metadata Key</th>
            <th>Metadata Value</th>
          </tr>
        </thead>
        <tbody>
          {Array.from(meta, ([key, value]) => (
            <tr>
              <td>{key}</td>
              <td>{value}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  function renderTable(tableData: string[][]) {
    return (
      <table className="bp3-html-table bp3-html-table-striped bp3-small">
        <thead>
          <tr>
            {tableData[0].map((cell, i) => (
              <th key={`header-${i}`}>{cell}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {tableData.slice(1).map((row, i) => (
            <tr key={`row-${i}`}>
              {row.map((cell, j) => (
                <td key={`cell-${i}-${j}`}> {cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  const NOT_APPLICABLE = <Blockquote> Not Applicable </Blockquote>;
  const NOT_LOADED = <Blockquote> Not Loaded </Blockquote>;

  function renderCsvTab() {
    if (currentFile !== null && currentFile.data !== null) {
      const index = currentFile.data.findIndex(f => "tableData" in f);
      if (index > -1) {
        const csvData = currentFile.data[index] as CsvFeature;
        return (
          <div className="vertical-container">
            {renderTable(csvData.tableData)}
          </div>
        );
      } else {
        return NOT_APPLICABLE;
      }
    }
    return NOT_LOADED;
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
          <Tab id="raw" title="File Details" panel={renderFileDetailTab()} />
          <Tab id="csv" title="CSV Details" panel={renderCsvTab()} />
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
