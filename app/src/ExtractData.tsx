import React, {
  ChangeEvent,
  FunctionComponent,
  ReactNode,
  useEffect,
  useRef,
  useState
} from "react";
import { RouteComponentProps } from "@reach/router";
import {
  Button,
  Card,
  ControlGroup,
  FormGroup,
  H6,
  HTMLSelect,
  Icon,
  InputGroup,
  Spinner,
  Tab,
  TabId,
  Tabs,
  Tag,
  TagInput
} from "@blueprintjs/core";
import { Intent } from "@blueprintjs/core/lib/esm/common/intent";
import {
  Cell,
  Column,
  IRegion,
  SelectionModes,
  Table
} from "@blueprintjs/table";
import { fileExtractCounts } from "./mockData";

interface DateFormatMapping {
  columnName: string;
  dateFormat: string;
  newColumnName: string;
}

interface FileExtractCount {
  filename: string;
  count: number;
  absolutePath: string;
  extractedData: string[][];
}

const ExtractData: FunctionComponent<RouteComponentProps> = () => {
  const [activeTab, setActiveTab] = useState<string>("csv-group-1");
  const [columns, seColumns] = useState<ReactNode[]>([
    "CustomerName",
    "Address"
  ]);
  const [regexExpression, setRegexExpression] = useState("");
  const [extractor, setExtractor] = useState<string>("Regex Content Body");
  const [csvColumns, setCsvColumns] = useState<ReactNode[]>(["Customer ID"]);
  const [excelColumns, setExcelColumns] = useState<ReactNode[]>([
    "Customer ID"
  ]);
  const [dateFormat, setDateFormat] = useState<DateFormatMapping>({
    columnName: "",
    dateFormat: "",
    newColumnName: ""
  });
  const [dateFormats, setDateFormats] = useState<DateFormatMapping[]>([]);
  const [extractDataTable, setExtractDataTable] = useState<string[][]>([]);
  const [files, setFiles] = useState<FileExtractCount[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const fileTableRef = useRef(null);

  useEffect(() => {
    while (
      dateFormats.findIndex(df => !columns.includes(df.columnName)) !== -1
    ) {
      const index = dateFormats.findIndex(
        df => !columns.includes(df.columnName)
      );
      if (index >= 0) {
        console.log(index);
        dateFormats.splice(index, 1);
        setDateFormats([...dateFormats]);
      }
    }
  }, [columns]);

  function renderColumnCounter(left: number, right: number, suffix: string) {
    let intent: Intent = Intent.NONE;

    if (columns.length === csvColumns.length) {
      intent = Intent.SUCCESS;
    } else if (columns.length > csvColumns.length) {
      intent = Intent.WARNING;
    } else {
      intent = Intent.DANGER;
    }
    return (
      <div className="box">
        {
          <Tag minimal={true} intent={intent}>
            {left}/{right}
          </Tag>
        }{" "}
        {suffix}
      </div>
    );
  }

  function renderExtractorForm(ex: string) {
    function countCaptureGroup(regex: string) {
      try {
        const groups = new RegExp(regex + "|").exec("");
        if (groups) {
          return groups.length - 1;
        }
      } catch (err) {
        return 0;
      }
      return 0;
    }

    if (ex === "Regex Content Body") {
      return (
        <FormGroup
          label="Regex Expression"
          labelInfo={renderColumnCounter(
            countCaptureGroup(regexExpression),
            columns.length,
            "capture groups"
          )}
        >
          <InputGroup
            placeholder="(.*)\s+(.*))"
            value={regexExpression}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              setRegexExpression(e.target.value)
            }
            style={{ fontFamily: "Consolas" }}
          />
        </FormGroup>
      );
    } else if (ex === "CSV Content") {
      return (
        <FormGroup
          label="CSV Headers"
          labelInfo={renderColumnCounter(
            csvColumns.length,
            columns.length,
            "headers"
          )}
        >
          <TagInput
            values={csvColumns}
            placeholder="csv header values..."
            onChange={handleCsvColumnChange}
            tagProps={{ minimal: true }}
          />
        </FormGroup>
      );
    } else if (ex === "Excel Content") {
      return (
        <div>
          <FormGroup label="Sheetname">
            <InputGroup placeholder="e.g Sheet1, Sheet2" />
          </FormGroup>
          <FormGroup
            label="Excel Headers"
            labelInfo={renderColumnCounter(
              excelColumns.length,
              columns.length,
              "headers"
            )}
          >
            <TagInput
              values={excelColumns}
              placeholder="Excel Header values..."
              onChange={handleExcelColumnChange}
              tagProps={{ minimal: true }}
            />
          </FormGroup>
        </div>
      );
    }
    return <div> No Extractor Form Rendered </div>;
  }

  function renderTimestampForm() {
    return (
      <Card elevation={1}>
        <H6>
          <Icon icon="calendar" /> Add Timestamp Columns (Optional)
        </H6>
        <FormGroup label="From Column">
          <HTMLSelect
            options={["", ...columns.map(c => (c ? c.toString() : ""))]}
            value={dateFormat.columnName}
            onChange={(e: ChangeEvent<HTMLSelectElement>) =>
              handleTimestampFormSelectChange(e.target.value)
            }
          />
        </FormGroup>

        <FormGroup label="New Column Name">
          <InputGroup
            placeholder=""
            value={dateFormat.newColumnName}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              setDateFormat({
                ...dateFormat,
                newColumnName: e.target.value
              })
            }
            disabled={!dateFormat.columnName}
          />
        </FormGroup>
        <FormGroup label="Date Format">
          <ControlGroup>
            <InputGroup
              placeholder="yyyyMMddHHmmss"
              value={dateFormat.dateFormat}
              onChange={(e: ChangeEvent<HTMLInputElement>) =>
                setDateFormat({
                  ...dateFormat,
                  dateFormat: e.target.value
                })
              }
              disabled={!dateFormat.columnName}
            />
            <Button
              icon="plus"
              onClick={handleAddTimestamp}
              disabled={!dateFormat.columnName}
            />
          </ControlGroup>
        </FormGroup>
        <table className="bp3-html-table bp3-small bp3-html-table-striped bp3-html-table-bordered">
          <thead>
            <tr>
              <th>Column</th>
              <th>New Name</th>
              <th>Date Pattern</th>
              <th>&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            {dateFormats.length === 0 ? (
              <tr>
                <td colSpan={4}> No Entries </td>
              </tr>
            ) : (
              dateFormats.map((df, index) => {
                return (
                  <tr key={index}>
                    <td>{df.columnName}</td>
                    <td>{df.newColumnName}</td>
                    <td>{df.dateFormat}</td>
                    <td>
                      <Button
                        icon="delete"
                        minimal={true}
                        onClick={() => handleRemoveDateFormat(index)}
                      />
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </Card>
    );
  }

  function renderGroupForm() {
    return (
      <div className="grid2">
        <div style={{ width: "500px" }} className="stack">
          <div className="box">
            <Button
              text="Simulate Extraction"
              icon={
                isLoading ? (
                  <div className="box">
                    <Spinner size={Spinner.SIZE_SMALL} />
                  </div>
                ) : (
                  "cut"
                )
              }
              onClick={handleSimulate}
            />
          </div>
          <Card elevation={1}>
            <H6>
              <Icon icon="th" /> Table Details
            </H6>
            <div>
              <FormGroup label="Table Name">
                <InputGroup placeholder="e.g Customers, Cars" />
              </FormGroup>
              <FormGroup
                label="Column Names"
                helperText={
                  <span>
                    Column count <Tag minimal={true}>{columns.length}</Tag>
                  </span>
                }
              >
                <TagInput
                  values={columns}
                  placeholder="Separate values with commas..."
                  onChange={handleChange}
                  tagProps={{ minimal: true }}
                />
              </FormGroup>
            </div>
          </Card>

          <Card elevation={1}>
            <H6>
              <Icon icon="cut" /> Extractor
            </H6>
            <FormGroup>
              <HTMLSelect
                value={extractor}
                onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                  setExtractor(e.target.value)
                }
              >
                <option>Regex Content Body</option>
                <option>CSV Content</option>
                <option>Excel Content</option>
              </HTMLSelect>
            </FormGroup>

            {renderExtractorForm(extractor)}
          </Card>

          {renderTimestampForm()}
        </div>
        <div className="grid2">
          <div className="filelist-box">
            <Table
              ref={fileTableRef}
              columnWidths={[200, 50]}
              numRows={files.length}
              selectionModes={SelectionModes.ROWS_AND_CELLS}
              selectedRegionTransform={e => {
                return {
                  rows: e.rows
                };
              }}
              onSelection={(r: IRegion[]) => {
                if (r.length > 0 && r[0].rows) {
                  setExtractDataTable(files[r[0].rows[0]].extractedData);
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
          {renderExtractTable()}
        </div>
      </div>
    );
  }

  function renderExtractTable() {
    return (
      <div className="extractDataTable">
        <Table
          numRows={extractDataTable.length}
          columnWidths={Array.from(
            Array(columns.length + dateFormats.length)
          ).map(() => 120)}
        >
          {[...columns, ...dateFormats.map(df => df.newColumnName)].map(
            (tag, colIndex) => (
              <Column
                key={colIndex}
                name={tag ? tag.toString() : ""}
                cellRenderer={rowIndex =>
                  renderTableDataCell(rowIndex, colIndex)
                }
              />
            )
          )}
        </Table>
      </div>
    );
  }

  const renderFileTableCell = (rowIndex: number) => {
    return <Cell>{files[rowIndex].filename}</Cell>;
  };

  const renderFileExtractCountCell = (rowIndex: number) => {
    return <Cell>{files[rowIndex].count}</Cell>;
  };

  const renderTableDataCell = (rowIndex: number, colIndex: number) => {
    return <Cell>{extractDataTable[rowIndex][colIndex]}</Cell>;
  };

  function render() {
    return (
      <div className="grid2">
        <div className="stack">
          <Tabs
            id="TabsExample"
            selectedTabId={activeTab}
            onChange={handleNavbarTabChange}
            renderActiveTabPanelOnly={true}
          >
            <Tab
              id="csv-group-1"
              title={
                <div className="box">
                  CSV-Customer-Group <Tag>56</Tag>
                </div>
              }
              panel={renderGroupForm()}
            />
            <Tab
              id="tika-content-group-2"
              title={
                <div className="box">
                  Excel-Cars-Group <Tag>0</Tag>
                </div>
              }
              panel={renderGroupForm()}
            />
            <Tabs.Expander />
          </Tabs>
        </div>
      </div>
    );
  }

  return render();

  function handleNavbarTabChange(tabId: TabId) {
    setActiveTab(tabId.toString());
    seColumns(["CustomerName", "Address"]);
    setRegexExpression("");
    setExtractor("Regex Content Body");
    setCsvColumns(["Customer ID"]);
    setExcelColumns(["Customer ID"]);
    setExtractDataTable([]);
    setFiles([]);
  }

  function handleChange(values: React.ReactNode[]) {
    seColumns(values);
  }

  function handleCsvColumnChange(values: React.ReactNode[]) {
    setCsvColumns(values);
  }

  function handleExcelColumnChange(values: React.ReactNode[]) {
    setExcelColumns(values);
  }

  function handleAddTimestamp() {
    setDateFormats([
      ...dateFormats,
      {
        columnName: dateFormat.columnName,
        dateFormat: dateFormat.dateFormat,
        newColumnName: dateFormat.newColumnName
      }
    ]);
    setDateFormat({
      columnName: "",
      dateFormat: "",
      newColumnName: ""
    });
  }

  function handleRemoveDateFormat(index: number) {
    dateFormats.splice(index, 1);
    setDateFormats([...dateFormats]);
  }

  function handleTimestampFormSelectChange(str: string) {
    setDateFormat({
      columnName: str,
      newColumnName: str + "_ts",
      dateFormat: dateFormat.dateFormat
    });
  }

  function handleSimulate() {
    function genTable(num: number) {
      const cols = Array.from(Array(columns.length).keys());
      const data = Array.from(Array(num).keys()).map(rowNumber => {
        return [...cols, ...dateFormats].map((colNumber, colIndex) => {
          return "data" + colIndex;
        });
      });
      return data;
    }

    // @ts-ignore
    fileTableRef.current.clearSelection();

    const newFile = fileExtractCounts.map(f => {
      const num = Math.floor(Math.random() * 20);
      return { ...f, count: num, extractedData: genTable(num) };
    });
    setIsLoading(true);
    setTimeout(() => {
      setFiles([...newFile]);
      setExtractDataTable([]);
      setIsLoading(false);
    }, 1000);
  }
};

export default ExtractData;
