import React, {
  ChangeEvent,
  FunctionComponent,
  ReactNode,
  useContext,
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
import { Extractor, Group, TimestampColumn } from "./api";
import groupRuleService from "./api/GroupRuleService";
import extractConfigService from "./api/ExtractConfigService";
import { AxiosResponse } from "axios";
import { StoreContext } from "./StoreContext";
import {
  CSV_EXTRACTOR,
  EXCEL_EXTRACTOR,
  TIKA_CONTENT_EXTRACTOR
} from "./extractConfigReducer";

interface FileExtractCount {
  filename: string;
  count: number;
  absolutePath: string;
  extractedData: string[][];
}

const ExtractData: FunctionComponent<RouteComponentProps> = () => {
  const context = useContext(StoreContext);

  const [activeGroup, setActiveGroup] = useState<Group>();
  const [tablename, setTablename] = useState<string>();
  const [columns, setColumns] = useState<string[]>([]);
  const [extractorTemplate, setExtractorTemplate] = useState<Extractor[]>([]);
  const [extractor, setExtractor] = useState<Extractor>();

  const [regexExpression, setRegexExpression] = useState("");
  const [csvColumns, setCsvColumns] = useState<ReactNode[]>([]);
  const [excelSheet, setExcelSheet] = useState("");
  const [excelColumns, setExcelColumns] = useState<ReactNode[]>([""]);

  const emptyTimestampColumn = {
    fromColumn: "",
    format: "",
    name: "",
    timezone: ""
  };
  const [dateFormat, setDateFormat] = useState<TimestampColumn>(
    emptyTimestampColumn
  );
  const [extractDataTable, setExtractDataTable] = useState<string[][]>([]);
  const [files, setFiles] = useState<FileExtractCount[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const fileTableRef = useRef(null);

  const [groups, setGroups] = useState<Group[]>([]);

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
      context.extractConfigAction?.init(activeGroup.groupId);
    }
  }, [activeGroup]);

  // when we load a extractConfig, we should update forms
  useEffect(() => {
    if (context.extractConfigState) {
      setTablename(context.extractConfigState.tableName);
      setColumns(context.extractConfigState.columnNames);
      if (context.extractConfigState.extractor) {
        setExtractor(context.extractConfigState.extractor);
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
  }, [context.extractConfigState]);

  // when we set extractor (either onload or selection) we should populate extractor forms
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
    context.extractConfigAction?.filterTimestampColumns(columns);
  }, [columns]);

  function renderColumnCountTag(left: number, right: number, suffix: string) {
    let intent: Intent = Intent.NONE;

    if (left === right) {
      intent = Intent.SUCCESS;
    } else if (right > left) {
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
              handleRegexExpressionOnBlur();
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
                handleExcelExtractorChange(excelSheet, excelColumns);
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
            value={dateFormat.fromColumn}
            onChange={(e: ChangeEvent<HTMLSelectElement>) =>
              handleTimestampFormSelectChange(e.target.value)
            }
          />
        </FormGroup>

        <FormGroup label="New Column Name">
          <InputGroup
            placeholder=""
            value={dateFormat.name}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              setDateFormat({
                ...dateFormat,
                name: e.target.value
              })
            }
            disabled={!dateFormat.name}
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
            disabled={!dateFormat.fromColumn}
          />
        </FormGroup>

        <FormGroup label="Date Format">
          <ControlGroup>
            <InputGroup
              placeholder="yyyyMMddHHmmss"
              value={dateFormat.format}
              onChange={(e: ChangeEvent<HTMLInputElement>) =>
                setDateFormat({
                  ...dateFormat,
                  format: e.target.value
                })
              }
              disabled={!dateFormat.fromColumn}
            />
            <Button
              icon="plus"
              onClick={handleAddTimestamp}
              disabled={!dateFormat.fromColumn}
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
            {context.extractConfigState.timestampColumns.length === 0 ? (
              <tr>
                <td colSpan={4}> No Entries </td>
              </tr>
            ) : (
              context.extractConfigState.timestampColumns.map((t, index) => {
                return (
                  <tr key={index}>
                    <td>{t.fromColumn}</td>
                    <td>{t.name}</td>
                    <td>{t.timezone}</td>
                    <td>{t.format}</td>
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
                <InputGroup
                  placeholder="e.g Customers, Cars"
                  value={tablename}
                  onChange={(e: ChangeEvent<HTMLInputElement>) =>
                    setTablename(e.target.value)
                  }
                  onBlur={() => handleTableNameOnBlur()}
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
                  values={columns}
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
                  handleExtractorChange(e.target.value)
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
                cellRenderer={rowIndex => renderFileExtractCountCell(rowIndex)}
              />
            </Table>
          </div>
          {renderExtractDataTable()}
        </div>
      </div>
    );
  }

  function renderExtractDataTable() {
    return (
      <div className="extractDataTable">
        <Table
          numRows={extractDataTable.length}
          columnWidths={Array.from(
            Array(
              columns.length +
                context.extractConfigState.timestampColumns.length
            )
          ).map(() => 120)}
        >
          {[
            ...columns,
            ...context.extractConfigState.timestampColumns.map(df => df.name)
          ].map((tag, colIndex) => (
            <Column
              key={colIndex}
              name={tag ? tag.toString() : ""}
              cellRenderer={rowIndex => renderTableDataCell(rowIndex, colIndex)}
            />
          ))}
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

  function handleNavbarTabChange(tabId: TabId) {
    const groupId: number = +tabId.toString();
    const grp = groups.find(g => g.groupId === groupId);
    if (grp) setActiveGroup(grp);
  }

  function handleTableNameOnBlur() {
    if (activeGroup && tablename)
      context.extractConfigAction?.updateTablename(
        activeGroup.groupId,
        tablename
      );
  }

  function handleColumnNamesChange(values: React.ReactNode[]) {
    const strs = values.map(node => (node ? node.toString() : ""));
    console.log(strs);
    setColumns(strs);
    context.extractConfigAction?.updateColumns(strs);
  }

  function handleRegexExpressionOnBlur() {
    if (activeGroup && regexExpression)
      context.extractConfigAction?.updateTikaContentRegexExtractor(
        regexExpression
      );
  }

  function handleCsvColumnChange(values: React.ReactNode[]) {
    setCsvColumns(values);
    context.extractConfigAction?.updateCSVExtractor(
      values.map(v => (v ? v.toString() : ""))
    );
  }

  function handleExcelColumnChange(values: React.ReactNode[]) {
    setExcelColumns(values);
    handleExcelExtractorChange(excelSheet, values);
  }

  function handleExcelExtractorChange(
    sheetName: string,
    values: React.ReactNode[]
  ) {
    setExcelSheet(sheetName);
    setExcelColumns(values);
    context.extractConfigAction?.updateExcelExtractor(
      sheetName,
      values.map(v => (v ? v.toString() : ""))
    );
  }

  function handleExtractorChange(extractorId: string) {
    const foundExtractor = extractorTemplate.find(
      ex => ex.extractorId === extractorId
    );
    if (foundExtractor) {
      setExtractor(foundExtractor);
      context.extractConfigAction?.changeExtractor(foundExtractor);
    }
  }

  function handleAddTimestamp() {
    context.extractConfigAction?.addTimestampColumn({
      fromColumn: dateFormat.fromColumn,
      format: dateFormat.format,
      name: dateFormat.name,
      timezone: dateFormat.timezone
    });
    setDateFormat(emptyTimestampColumn);
  }

  function handleRemoveDateFormat(index: number) {
    context.extractConfigAction?.removeTimestampColumn(index);
  }

  function handleTimestampFormSelectChange(str: string) {
    setDateFormat({
      fromColumn: str,
      name: str + "_ts",
      format: dateFormat.format,
      timezone: dateFormat.timezone
    });
  }

  function handleSimulate() {
    function genTable(num: number) {
      const cols = Array.from(Array(columns.length).keys());
      const data = Array.from(Array(num).keys()).map(rowNumber => {
        return [...cols, ...context.extractConfigState.timestampColumns].map(
          (colNumber, colIndex) => {
            return "data" + colIndex;
          }
        );
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
