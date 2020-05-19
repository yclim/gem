import React, {
  ChangeEvent,
  FunctionComponent,
  ReactNode,
  useEffect,
  useRef,
  useState
} from "react";
import { RouteComponentProps } from "@reach/router";
import * as moment from "moment-timezone";

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
import { ExtractConfig, Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import extractConfigService from "./api/ExtractConfigService";
import { AxiosResponse } from "axios";

interface DateFormatMapping {
  columnName: string;
  dateFormat: string;
  newColumnName: string;
  timezone?: string;
}

interface FileExtractCount {
  filename: string;
  count: number;
  absolutePath: string;
  extractedData: string[][];
}

const ExtractData: FunctionComponent<RouteComponentProps> = () => {
  const [activeGroup, setActiveGroup] = useState<Group>();
  const [columns, setColumns] = useState<string[]>(["CustomerName", "Address"]);
  const [regexExpression, setRegexExpression] = useState("");
  const [extractor, setExtractor] = useState<string>("Regex Content Body");
  const [csvColumns, setCsvColumns] = useState<ReactNode[]>(["Customer ID"]);
  const [excelColumns, setExcelColumns] = useState<ReactNode[]>([
    "Customer ID"
  ]);
  const [dateFormat, setDateFormat] = useState<DateFormatMapping>({
    columnName: "",
    dateFormat: "",
    newColumnName: "",
    timezone: ""
  });
  const [dateFormats, setDateFormats] = useState<DateFormatMapping[]>([]);
  const [extractDataTable, setExtractDataTable] = useState<string[][]>([]);
  const [files, setFiles] = useState<FileExtractCount[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const fileTableRef = useRef(null);

  const [groups, setGroups] = useState<Group[]>([]);
  const [extractConfig, setExtractConfig] = useState<ExtractConfig>();

  useEffect(() => {
    groupRuleService.getGroups().then((resp: AxiosResponse<Group[]>) => {
      const grps = resp.data;
      setGroups(grps);
      setActiveGroup(grps[0]);
    });
  }, []);

  useEffect(() => {
    if (activeGroup)
      extractConfigService.getExtractConfig(activeGroup.groupId).then(resp => {
        const config: ExtractConfig = resp.data
          ? resp.data
          : {
              columnNames: [],
              groupId: activeGroup.groupId,
              tableName: activeGroup.name,
              timestampColumns: []
            };

        setExtractConfig(config);
      });
  }, [activeGroup]);

  useEffect(() => {
    while (
      dateFormats.findIndex(df => !columns.includes(df.columnName)) !== -1
    ) {
      const index = dateFormats.findIndex(
        df => !columns.includes(df.columnName)
      );
      if (index >= 0) {
        dateFormats.splice(index, 1);
        setDateFormats([...dateFormats]);
      }
    }
  }, [columns]);

  function renderColumnCountTag(left: number, right: number, suffix: string) {
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
        const captureGroups = new RegExp(regex + "|").exec("");
        if (captureGroups) {
          return captureGroups.length - 1;
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
          labelInfo={renderColumnCountTag(
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
          labelInfo={renderColumnCountTag(
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
            labelInfo={renderColumnCountTag(
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

        <FormGroup label="Timezone">
          <HTMLSelect
            options={[
              "",
              ...moment.tz.names().map(c => (c ? c.toString() : ""))
            ]}
            value={dateFormat.timezone}
            onChange={(e: ChangeEvent<HTMLSelectElement>) =>
              setDateFormat({
                ...dateFormat,
                timezone: e.target.value
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
              <th>Timezone</th>
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
                    <td>{df.timezone}</td>
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
    const config = extractConfig;
    if (config)
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
                  <InputGroup
                    placeholder="e.g Customers, Cars"
                    value={config.tableName}
                    onChange={(e: ChangeEvent<HTMLInputElement>) =>
                      handleTableNameChange(e.target.value)
                    }
                    onBlur={() => saveExtractConfigChange(config)}
                  />
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
                    values={config.columnNames}
                    placeholder="Separate values with commas..."
                    onChange={handleColumnNamesChange}
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
                  cellRenderer={rowIndex =>
                    renderFileExtractCountCell(rowIndex)
                  }
                />
              </Table>
            </div>
            {renderExtractDataTable()}
          </div>
        </div>
      );

    return <div>Fail renderGroupForm (config undefined or null)</div>;
  }

  function renderExtractDataTable() {
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
            selectedTabId={activeGroup?.groupId}
            onChange={handleNavbarTabChange}
            renderActiveTabPanelOnly={true}
          >
            {groups.map(g => (
              <Tab
                key={g.name}
                id={g.groupId}
                title={
                  <div className="box">
                    {g.name} <Tag>0</Tag>
                  </div>
                }
                panel={renderGroupForm()}
              />
            ))}
            <Tabs.Expander />
          </Tabs>
        </div>
      </div>
    );
  }

  return render();

  function saveExtractConfigChange(config: ExtractConfig) {
    if (config) {
      extractConfigService.saveExtractConfig(config).then();
    }
  }

  function handleTableNameChange(value: string) {
    const config = extractConfig;
    if (config) {
      setExtractConfig({ ...config, tableName: value });
    }
  }

  function handleColumnNamesChange(values: React.ReactNode[]) {
    if (extractConfig) {
      const config: ExtractConfig = {
        ...extractConfig,
        columnNames: values.map(node => (node ? node.toString() : ""))
      };

      setExtractConfig(config);
      saveExtractConfigChange(config);
    }
  }

  function handleNavbarTabChange(tabId: TabId) {
    const groupId: number = +tabId.toString();
    const grp = groups.find(g => g.groupId === groupId);
    if (grp) setActiveGroup(grp);
    setRegexExpression("");
    setExtractor("Regex Content Body");
    setCsvColumns(["Customer ID"]);
    setExcelColumns(["Customer ID"]);
    setExtractDataTable([]);
    setFiles([]);
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
        newColumnName: dateFormat.newColumnName,
        timezone: dateFormat.timezone
      }
    ]);
    setDateFormat({
      columnName: "",
      dateFormat: "",
      newColumnName: "",
      timezone: ""
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
      dateFormat: dateFormat.dateFormat,
      timezone: dateFormat.timezone
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
