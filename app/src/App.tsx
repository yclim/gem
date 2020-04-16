import React from "react";
import { render } from "react-dom";
import "@blueprintjs/icons/lib/css/blueprint-icons.css";
import "@blueprintjs/core/lib/css/blueprint.css";

import { Link, Router } from "@reach/router";
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
                icon="wrench"
                text="Configure Groups"
              />
            </Link>
            <Link to="/files">
              <Button
                className={Classes.MINIMAL}
                icon="document"
                text="Browse Files"
              />
            </Link>
            <Button className={Classes.MINIMAL} icon="cut" text="Extract" />
          </NavbarGroup>
        </Navbar>
      </div>
      <Router>
        <EditGroups path="/" />
        <BrowseFiles path="/files" />
      </Router>
    </div>
  );
};

render(<App />, document.getElementById("root"));
