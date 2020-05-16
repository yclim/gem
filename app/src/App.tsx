import React from "react";
import { render } from "react-dom";
import "@blueprintjs/icons/lib/css/blueprint-icons.css";
import "@blueprintjs/core/lib/css/blueprint.css";

import { Link, RouteComponentProps, Router } from "@reach/router";
import {
  Alignment,
  Button,
  Classes,
  Navbar,
  NavbarDivider,
  NavbarGroup,
  NavbarHeading
} from "@blueprintjs/core";
import "./App.scss";
import EditGroups from "./EditGroups";
import BrowseFiles from "./BrowseFiles";
import ExtractData from "./ExtractData";
import ExportSpec from "./ExportSpec";
import { StoreProvider } from "./StoreContext";

const App = () => {
  return (
    <div className="container">
      <div className="top-nav-section">
        <Navbar>
          <NavbarGroup align={Alignment.LEFT}>
            <NavbarHeading>GEM</NavbarHeading>
            <NavbarDivider />
            <Link to="/">
              <Button
                className={Classes.MINIMAL}
                icon="document"
                text="Browse Files"
              />
            </Link>
            <Link to="/groups">
              <Button
                className={Classes.MINIMAL}
                icon="wrench"
                text="Configure Groups"
              />
            </Link>
            <Link to="/extract">
              <Button className={Classes.MINIMAL} icon="cut" text="Extract" />
            </Link>
            <Link to="/export">
              <Button className={Classes.MINIMAL} icon="export" text="Export" />
            </Link>
          </NavbarGroup>
        </Navbar>
      </div>
      <Router>
        <RouterPage
          path="/groups"
          pageComponent={
            <StoreProvider>
              <EditGroups />
            </StoreProvider>
          }
        />

        <BrowseFiles path="/" />
        <ExtractData path="/extract" />
        <ExportSpec path="/export" />
      </Router>
    </div>
  );
};

const RouterPage = (
  props: { pageComponent: JSX.Element } & RouteComponentProps
) => props.pageComponent;

render(<App />, document.getElementById("root"));
