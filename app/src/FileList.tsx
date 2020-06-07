import { Cell } from "@blueprintjs/table";
import {
  Blockquote,
  Card,
  Elevation,
  Tab,
  TabId,
  Tabs,
  Tag
} from "@blueprintjs/core";
import React, { FunctionComponent, useState } from "react";
import { CsvFeature, ExcelFeature, File, TikaFeature } from "./api";
import fileService from "./api/FileService";
import { ColDef } from "ag-grid-community";
import { AgGridReact } from "ag-grid-react";

interface IProps {
  files: File[];
  setFiles: (fs: File[]) => void;
}

const FileList: FunctionComponent<IProps> = ({ files, setFiles }) => {
  const [activeTab, setActiveTab] = useState("raw");
  const [currentFile, setCurrentFile] = useState<File | null>(null);
  const [activeExcelTab, setActiveExcelTab] = useState<string | undefined>();

  function handleNavbarTabChange(tabId: TabId) {
    setActiveTab(tabId.toString());
  }

  function handleExcelTabChange(tabId: TabId) {
    setActiveExcelTab(tabId.toString());
  }

  function handleFileSelection(filename: string, directory: string) {
    fileService.getFile(filename, directory).then(f => {
      setCurrentFile(f.data);
    });
  }

  function renderTable(tableData: string[][]) {
    const columnDef: ColDef[] = tableData[0].map((c, i) => {
      return { headerName: c, field: i + "" };
    });
    const rowData = tableData.slice(1).map(r => {
      return { ...r };
    });
    return (
      <div className="vertical-scroll-only ag-theme-alpine">
        <AgGridReact
          columnDefs={columnDef}
          rowData={rowData}
          rowHeight={35}
          headerHeight={35}
          suppressCellSelection={true}
          pagination={true}
          paginationAutoPageSize={true}
        />
      </div>
    );
  }

  function renderMetadata(meta: Map<string, string>) {
    const keys = Object.keys(meta);
    const values = Object.values(meta);
    const pairs = keys.map((k, i) => [k, values[i]]);

    return (
      <div className="vertical-scroll-only">
        <table className="bp3-html-table bp3-html-table-striped keyval-table">
          <thead>
            <tr>
              <th>Metadata Key</th>
              <th>Metadata Value</th>
            </tr>
          </thead>
          <tbody>
            {pairs.map(arr => (
              <tr key={`tr-${arr[0]}`}>
                <td>{arr[0]}</td>
                <td>{arr[1]}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
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
              <tr>
                <td>Modified Time</td>
                <td>{currentFile?.lastModifiedTime}</td>
              </tr>
              <tr>
                <td>Created Time</td>
                <td>{currentFile?.creationTime}</td>
              </tr>
              <tr>
                <td>Matched Group(s)</td>
                <td>
                  {currentFile?.matchedGroupNames?.map((object, i) => (
                    <Tag key={i} style={{ marginRight: "5px" }}>
                      {object}
                    </Tag>
                  ))}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      );
    } else {
      return <div />;
    }
  }

  const NOT_APPLICABLE = <Blockquote> Not Applicable </Blockquote>;
  const NOT_LOADED = <Blockquote> Not Loaded </Blockquote>;

  function renderCsvTab() {
    if (currentFile !== null && currentFile.data !== null) {
      const index = currentFile.data.findIndex(f => "tableData" in f);
      if (index > -1) {
        const data = currentFile.data[index] as CsvFeature;
        return (
          <div className="vertical-scroll-only">
            {renderTable(data.tableData)}
          </div>
        );
      } else {
        return NOT_APPLICABLE;
      }
    }
    return NOT_LOADED;
  }

  function renderExcelTab() {
    if (currentFile !== null && currentFile.data !== null) {
      const index = currentFile.data.findIndex(f => "sheetTableData" in f);
      if (index > -1) {
        // tried multiple ways to map data.sheetTableData but fail
        // not sure why Array.from(data.sheetTableData).map(...) doesn't work
        // this is the only way i tried that work so far
        const data = currentFile.data[index] as ExcelFeature;

        const sheets = Object.keys(data.sheetTableData);
        const tables = Object.values(data.sheetTableData);
        const sheetsTables = sheets.map((s, i) => [s, tables[i]]);

        return (
          <div>
            <Tabs
              id="ExcelSheetTabs"
              selectedTabId={activeExcelTab}
              onChange={handleExcelTabChange}
            >
              {sheetsTables.map((arr, i) => (
                <Tab
                  key={`${arr[0]}-${i}`}
                  id={`${arr[0]}-${i}`}
                  title={arr[0]}
                  panel={renderTable(arr[1])}
                />
              ))}
            </Tabs>
          </div>
        );
      } else {
        return NOT_APPLICABLE;
      }
    }
    return NOT_LOADED;
  }

  function renderTikaMetaTab() {
    if (currentFile !== null && currentFile.data !== null) {
      const index = currentFile.data.findIndex(f => "content" in f);
      if (index > -1) {
        const data = currentFile.data[index] as TikaFeature;
        return renderMetadata(data.metadata);
      } else {
        return NOT_APPLICABLE;
      }
    }
    return NOT_LOADED;
  }

  function renderTikaContentTab() {
    if (currentFile !== null && currentFile.data !== null) {
      const index = currentFile.data.findIndex(f => "content" in f);
      if (index > -1) {
        const data = currentFile.data[index] as TikaFeature;
        return (
          <Card elevation={Elevation.ZERO} className="detail-box">
            <pre> {data.content} </pre>
          </Card>
        );
      } else {
        return NOT_APPLICABLE;
      }
    }
    return NOT_LOADED;
  }

  return (
    <div className="grid2">
      <div className="box filelist-box ag-theme-alpine">
        <AgGridReact
          columnDefs={[
            {
              headerName: "#",
              valueGetter: "node.rowIndex + 1",
              width: 30
            },
            { headerName: "Matched filenames", field: "fileName", width: 250 }
          ]}
          rowData={files}
          rowHeight={35}
          headerHeight={35}
          onRowClicked={e =>
            handleFileSelection(
              files[e.rowIndex].fileName,
              files[e.rowIndex].directory
            )
          }
          suppressCellSelection={true}
          rowSelection={"single"}
        />
      </div>
      <div className="grid1">
        <Tabs
          id="DetailsTab"
          selectedTabId={activeTab}
          onChange={handleNavbarTabChange}
          renderActiveTabPanelOnly={true}
        >
          <Tab id="raw" title="File Details" panel={renderFileDetailTab()} />
          <Tab
            id="tikaMeta"
            title="Tika Metadata"
            panel={renderTikaMetaTab()}
          />
          <Tab
            id="tikaContent"
            title="Tika Content"
            panel={renderTikaContentTab()}
          />
          <Tab id="csv" title="CSV Details" panel={renderCsvTab()} />
          <Tab id="xls" title="EXCEL Details" panel={renderExcelTab()} />
          <Tabs.Expander />
        </Tabs>
      </div>
    </div>
  );
};

export default FileList;
