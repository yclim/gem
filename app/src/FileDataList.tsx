import React, { FunctionComponent, useContext, useState } from "react";
import { StoreContext } from "./StoreContext";
import { ExtractedData, FileCount } from "./api";
import extractConfigService from "./api/ExtractConfigService";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine.css";
import { ColDef } from "ag-grid-community";

interface IFileDataListProps {
  fileCounts: FileCount[];
}

interface IFileCountProps {
  onFileSelection: (absolutePath: string) => void;
  fileCounts: FileCount[];
}

interface IDataListProps {
  columns: string[]; // derived columns from extractDataState
  extractedData: ExtractedData;
}

const FileDataList: FunctionComponent<IFileDataListProps> = ({
  fileCounts
}) => {
  const context = useContext(StoreContext);
  const [extractedData, setExtractedData] = useState<ExtractedData>({
    headers: [],
    records: []
  });
  const onFileSelection = (absolutePath: string) => {
    if (context.extractConfigState) {
      extractConfigService
        .getExtractedRecords(context.extractConfigState.groupId, absolutePath)
        .then(resp => {
          if (resp.data) setExtractedData(resp.data);
        });
    }
  };

  const timestampColumns = context.extractConfigState.timestampColumns
    ? context.extractConfigState.timestampColumns
    : [];
  const columns = [
    ...context.extractConfigState.columnNames,
    ...timestampColumns.map(df => df.name)
  ];

  return (
    <div className="grid2">
      <FileCountList
        fileCounts={fileCounts}
        onFileSelection={onFileSelection}
      />
      <DataList columns={columns} extractedData={extractedData} />
    </div>
  );
};

const FileCountList: FunctionComponent<IFileCountProps> = ({
  onFileSelection,
  fileCounts
}) => {
  const columnDef: ColDef[] = [
    { headerName: "Filename", field: "filename", width: 220 },
    { headerName: "Cnt", field: "count", width: 60 }
  ];
  return (
    <div className="filelist-box ag-theme-alpine">
      <AgGridReact
        columnDefs={columnDef}
        rowData={fileCounts}
        rowHeight={35}
        headerHeight={35}
        onRowClicked={e => onFileSelection(fileCounts[e.rowIndex].absolutePath)}
        suppressCellSelection={true}
      />
    </div>
  );
};

const DataList: FunctionComponent<IDataListProps> = ({
  columns,
  extractedData
}) => {
  const columnDef: ColDef[] = columns.map((c, i) => {
    return { headerName: c, field: i + "" };
  });
  const rowData = extractedData.records.map(r => {
    return { ...r };
  });
  return (
    <div className="extractDataTable ag-theme-alpine">
      <AgGridReact
        columnDefs={columnDef}
        rowData={rowData}
        rowHeight={35}
        headerHeight={35}
        suppressCellSelection={true}
      />
    </div>
  );
};

export default FileDataList;
