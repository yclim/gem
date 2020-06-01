import React, { FunctionComponent, useContext, useRef, useState } from "react";
import {
  Cell,
  Column,
  IRegion,
  SelectionModes,
  Table
} from "@blueprintjs/table";
import { StoreContext } from "./StoreContext";
import { ExtractedData, FileCount } from "./api";

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
  const [extractedData, setExtractedData] = useState<ExtractedData>();
  const onFileSelection = (filePath: string) => {
    // TODO call api to get extracted data from given path
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
      <DataList
        columns={columns}
        extractedData={extractedData ? extractedData : { header: [], data: [] }}
      />
    </div>
  );
};

const FileCountList: FunctionComponent<IFileCountProps> = ({
  onFileSelection,
  fileCounts
}) => {
  const context = useContext(StoreContext);
  const fileTableRef = useRef(null);
  return (
    <div className="filelist-box">
      <Table
        ref={fileTableRef}
        columnWidths={[200, 50]}
        numRows={fileCounts.length}
        selectionModes={SelectionModes.ROWS_AND_CELLS}
        selectedRegionTransform={e => {
          return {
            rows: e.rows
          };
        }}
        onSelection={(r: IRegion[]) => {
          if (r.length > 0 && r[0].rows) {
            onFileSelection(fileCounts[r[0].rows[0]].filename);
          }
        }}
      >
        <Column
          name="Filename"
          cellRenderer={rowIndex => renderFileTableCell(rowIndex)}
        />
        <Column
          name="#"
          cellRenderer={rowIndex => renderFileExtractCountCell(rowIndex)}
        />
      </Table>
    </div>
  );

  const renderFileTableCell = (rowIndex: number) => {
    return <Cell>{fileCounts[rowIndex].filename}</Cell>;
  };

  const renderFileExtractCountCell = (rowIndex: number) => {
    return <Cell>{fileCounts[rowIndex].count}</Cell>;
  };
};

const DataList: FunctionComponent<IDataListProps> = ({
  columns,
  extractedData
}) => {
  return (
    <div className="extractDataTable">
      <Table
        numRows={extractedData.data.length}
        columnWidths={Array.from(Array(columns.length)).map(() => 120)}
      >
        {columns.map((tag, colIndex) => (
          <Column
            key={colIndex}
            name={tag ? tag.toString() : ""}
            cellRenderer={rowIndex => (
              <Cell>{extractedData.data[rowIndex][colIndex]}</Cell>
            )}
          />
        ))}
      </Table>
    </div>
  );
};

export default FileDataList;
