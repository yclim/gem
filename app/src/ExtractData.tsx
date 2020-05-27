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
import { ExtractConfig, Extractor, Group } from "./api";
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

// hardcoded list the extractors
const CSV_EXTRACTOR = "innohack.gem.service.extract.CSVExtractor";
const EXCEL_EXTRACTOR = "innohack.gem.service.extract.ExcelExtractor";
const TIKA_CONTENT_EXTRACTOR =
  "innohack.gem.service.extract.TikaContentExtractor";

const ExtractData: FunctionComponent<RouteComponentProps> = () => {
  const [activeGroup, setActiveGroup] = useState<Group>();
  const [columns, setColumns] = useState<string[]>([]);
  const [extractorTemplate, setExtractorTemplate] = useState<Extractor[]>([]);
  const [extractor, setExtractor] = useState<Extractor>();

  const [regexExpression, setRegexExpression] = useState("");
  const [csvColumns, setCsvColumns] = useState<ReactNode[]>([]);
  const [excelSheet, setExcelSheet] = useState("");
  const [excelColumns, setExcelColumns] = useState<ReactNode[]>([""]);
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
    const extractorTemplatePromise = extractConfigService
      .getExtractorTemplates()
      .then((resp: AxiosResponse<Extractor[]>) => {
        setExtractorTemplate(resp.data);
      });
    const groupsPromise = groupRuleService
      .getGroups()
      .then((resp: AxiosResponse<Group[]>) => {
        const grps = resp.data;
        setGroups(grps);
        return grps;
      });

    Promise.all<void, Group[]>([extractorTemplatePromise, groupsPromise]).then(
      ([nothing, grps]) => {
        setActiveGroup(grps[0]);
      }
    );
  }, []);

  useEffect(() => {
    if (activeGroup) {
      extractConfigService.getExtractConfig(activeGroup.groupId).then(resp => {
        const config: ExtractConfig = resp.data
          ? resp.data
          : {
              columnNames: [],
              groupId: activeGroup.groupId,
              tableName: activeGroup.name,
              timestampColumns: [],
              extractor: null
            };

        setColumns(config.columnNames);
        setExtractConfig(config);
      });
    }
  }, [activeGroup]);

  // when we loaded a extractConfig, we should set extractor
  useEffect(() => {
    if (extractConfig) {
      if (extractConfig.extractor) {
        setExtractor(extractConfig.extractor);
      } else {
        // Use tika content extractor by default
        setExtractor(
          extractorTemplate.find(
            ex => ex.extractorId === TIKA_CONTENT_EXTRACTOR
          )
        );
        setRegexExpression("");
      }
    }
  }, [extractConfig]);

  // when we set extractor, either onload or selection, we should populate the forms
  useEffect(() => {
    if (extractor) {
      switch (extractor.extractorId) {
        case TIKA_CONTENT_EXTRACTOR: {
          const value = extractor.params[0].value;
          if (value) setRegexExpression(value);
          else setRegexExpression("");
          break;
        }
        case CSV_EXTRACTOR: {
          const columnValue = extractor.params[0].value;
          if (columnValue) setCsvColumns(columnValue.split(","));
          else setCsvColumns([]);
          break;
        }
        case EXCEL_EXTRACTOR: {
          const value = extractor.params[0].value;
          if (value) setExcelSheet(value);
          else setExcelSheet("");

          const columnValue = extractor.params[1].value;
          if (columnValue) setExcelColumns(columnValue.split(","));
          else setExcelColumns([]);
          break;
        }
        default: {
          throw new Error("Extractor not supported:" + extractor.extractorId);
        }
      }
    }
  }, [extractor]);

  // when remove columns, we should remove all dateFormats that relies on remove columns
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

  // when we change csv/excel columns (tag input component), we should save form
  useEffect(() => {
    if (extractConfig) {
      saveExtractConfigChange(extractConfig);
    }
  }, [csvColumns, excelColumns]);

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

  function renderExtractorForm() {
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

    if (extractor && extractor.extractorId === TIKA_CONTENT_EXTRACTOR) {
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
            onBlur={() => {
              if (extractConfig) saveExtractConfigChange(extractConfig);
            }}
            style={{ fontFamily: "Consolas" }}
          />
        </FormGroup>
      );
    } else if (extractor && extractor.extractorId === CSV_EXTRACTOR) {
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
    } else if (extractor && extractor.extractorId === EXCEL_EXTRACTOR) {
      return (
        <div>
          <FormGroup label="Sheetname">
            <InputGroup
              placeholder="e.g Sheet1, Sheet2"
              value={excelSheet}
              onChange={(e: ChangeEvent<HTMLInputElement>) =>
                setExcelSheet(e.target.value)
              }
              onBlur={() => {
                if (extractConfig) saveExtractConfigChange(extractConfig);
              }}
            />
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
                  value={extractor?.extractorId}
                  onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                    setExtractor(
                      extractorTemplate.find(
                        ex => ex.extractorId === e.target.value
                      )
                    )
                  }
                >
                  <option value={TIKA_CONTENT_EXTRACTOR}>
                    Regex Content Body
                  </option>
                  <option value={CSV_EXTRACTOR}>CSV Content</option>
                  <option value={EXCEL_EXTRACTOR}>Excel Content</option>
                </HTMLSelect>
              </FormGroup>

              {renderExtractorForm()}
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

    return <div>Loading...</div>;
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
    if (extractor) {
      let saveExtractor = { ...extractor };
      switch (extractor.extractorId) {
        case TIKA_CONTENT_EXTRACTOR: {
          const param1 = extractor.params[0];
          saveExtractor = {
            ...extractor,
            params: [{ ...param1, value: regexExpression }]
          };
          break;
        }
        case CSV_EXTRACTOR: {
          const param1 = extractor.params[0];
          saveExtractor = {
            ...extractor,
            params: [{ ...param1, value: csvColumns.join(",") }]
          };
          break;
        }
        case EXCEL_EXTRACTOR: {
          const param1 = extractor.params[0];
          const param2 = extractor.params[1];
          saveExtractor = {
            ...extractor,
            params: [
              { ...param1, value: excelSheet },
              { ...param2, value: excelColumns.join(",") }
            ]
          };
          break;
        }
        default: {
          throw new Error(
            "Extractor not supported:" + config.extractor?.extractorId
          );
        }
      }
      const saveConfig = { ...config, extractor: saveExtractor };
      extractConfigService.saveExtractConfig(saveConfig).then();
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
      setColumns(values.map(node => (node ? node.toString() : "")));
      setExtractConfig(config);
      saveExtractConfigChange(config);
    }
  }

  function handleNavbarTabChange(tabId: TabId) {
    const groupId: number = +tabId.toString();
    const grp = groups.find(g => g.groupId === groupId);
    if (grp) setActiveGroup(grp);
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
