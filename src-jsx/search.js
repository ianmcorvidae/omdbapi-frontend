var React = require('react');
var $ = require('jquery');

var SearchDetails = React.createClass({
  render: function() {
    var omdb = {};
    var keys = ["Title", "Year", "imdbID", "Rated", "Released", "Runtime", "Genre", "Director", "Writer", "Actors", "Plot", "Language", "Country", "Awards", "Poster"];
    keys.forEach(function (key, index) {
        omdb[key] = this.props.data.omdb[index];
    }, this);
    var extraRows = ["Rated", "Released", "Runtime", "Genre", "Director", "Writer", "Actors", "Awards"].map(function (name) {
        if (omdb[name]) {
          return <tr key={name}><th>{name}</th><td>{omdb[name]}</td></tr>;
        }
    });
    if (this.props.data.wikidata) {
        var wikilinks = this.props.data.wikidata.map(function (linkspec, idx, arr) {
            var last = (idx === arr.length - 1);
            return <span key={linkspec[0]}><a target="_blank" href={linkspec[1]}>{linkspec[0]}</a>{last ? "" : ", "}</span>;
        }, this);
        extraRows.push(<tr key="wiki"><th>Wikipedia</th><td>{wikilinks}</td></tr>);
    }
    var nrows = extraRows.length + 3; // +3: Plot, Language/Country, plus this row itself
    var poster = omdb["Poster"] ? <tr><td className="poster" rowSpan={nrows}><img src={omdb["Poster"]} /></td></tr> : null;

    return (
      <table className="details">
        <tr><th colSpan={omdb["Poster"] ? 3 : 2}>{omdb["Title"]}</th></tr>
        {poster}
        <tr><th>Plot</th><td>{omdb["Plot"] || 'Plot unlisted'}</td></tr>
        {extraRows}
        <tr><th>Language/Country</th><td>{omdb["Language"] || 'Unknown'}/{omdb["Country"] || 'Unknown'}</td></tr>
      </table>
    );
  }
});

var SearchResult = React.createClass({
  getInitialState: function() {
    return {expanded: false, expandedData: null, loading: false};
  },
  handleClick: function(e) {
    if (this.state.expanded) {
      this.setState({expanded: false});
    } else {
      this.setState({expanded: true});

      if (!this.state.expandedData) {
        this.setState({loading: true});
        $.ajax({
          url: this.props.url,
          data: {id: this.props.data[2]},
          type: 'GET',
          success: function(data) {
            this.setState({expandedData: data, loading: false});
          }.bind(this),
          error: function(xhr, status, err) {
            this.setState({loading: false});
            console.error(this.props.url, status, err.toString());
          }.bind(this)
        });
      }
    }
  },
  render: function() {
    var firstCell;
    if (this.state.expanded && this.state.expandedData) {
      firstCell = <td className="title"><SearchDetails data={this.state.expandedData} /></td>;
    } else if (this.state.expanded && this.state.loading) {
      firstCell = <td className="title loading"></td>;
    } else {
      firstCell = <td className="title">{this.props.data[0]}</td>;
    }
    return (
      <tr>
        <td className="expand" title={this.state.expanded ? "Show Less" : "Show More"} onClick={this.handleClick}>{this.state.expanded ? '-' : '+'}</td>
        {firstCell}
        <td className="year">{this.props.data[1]}</td>
        <td className="imdb"><a href={"http://www.imdb.com/title/" + this.props.data[2]} target="_blank">{this.props.data[2]}</a></td>
      </tr>
    );
  }
});

var SearchForm = React.createClass({
  getInitialState: function() {
    return {"query": this.props.initialQuery || "",
            "year": this.props.initialYear || ""}
  },
  updateHashState: function() {
    if (location.hash) {
      var startQuery = location.hash.slice(1).split('/');
      var query = decodeURIComponent(startQuery[0]);
      var year = startQuery.length > 1 ? decodeURIComponent(startQuery[1]) : "";
      this.setState({"query": query, "year": year});
      this.runSubmit(query, year);
    }
  },
  componentDidMount: function () {
    window.addEventListener('hashchange', this.updateHashState, false);
    this.updateHashState();
  },
  runSubmit: function(query, year) {
    if (!query) { return; }
    this.props.onSubmit(query, year);
    return;
  },
  handleSubmit: function(e) {
    e.preventDefault();
    var query = this.state.query.trim();
    var year = this.state.year.trim();
    location.hash = encodeURIComponent(query) + '/' + encodeURIComponent(year);
    this.runSubmit(query, year);
    return;
  },
  queryUpdate: function(e) {
    var query = React.findDOMNode(this.refs.query).value;
    this.setState({"query": query});
  },
  yearUpdate: function(e) {
    var year = React.findDOMNode(this.refs.year).value;
    this.setState({"year": year});
  },
  render: function() {
    return (
      <form className="searchForm" onSubmit={this.handleSubmit}>
        <input type="text" name="query" className="query" ref="query" placeholder="Movie Title" onChange={this.queryUpdate} value={this.state.query} />
        <input type="number" name="year" className="year" ref="year" placeholder="YYYY" onChange={this.yearUpdate} value={this.state.year} />
        <input type="submit" />
      </form>
    );
  }
});

var SearchBox = React.createClass({
  getInitialState: function() {
    return {results: [], loading: false};
  },
  handleSearchSubmit: function(query, year) {
    this.setState({loading: true});
    $.ajax({
      url: this.props.searchUrl,
      data: {q: query, y: year},
      type: 'GET',
      success: function(data) {
        this.setState({results: data.omdb, loading: false});
      }.bind(this),
      error: function(xhr, status, err) {
        this.setState({loading: false});
        console.error(this.props.searchUrl, status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    var results;
    var detailsUrl = this.props.detailsUrl;
    if (this.state.results && this.state.results.length > 0) {
      var rows = this.state.results.map(function (r) { return (<SearchResult key={r[2]} data={r} url={detailsUrl} />); });
      results = (
          <table>
            <thead><tr>
              <th></th>
              <th>Title/Detailed Information</th>
              <th>Year</th>
              <th>IMDB ID</th>
            </tr></thead>
            <tbody>{rows}</tbody>
          </table>
      );
    } else if (this.state.loading) {
      results = <div className="loading"></div>;
    } else if (this.state.results === null) {
      results = <p>No results found, or server error. Please try again.</p>;
    }
    return (
      <div>
        <SearchForm onSubmit={this.handleSearchSubmit} />
        {results}
      </div>
    );
  }
});

React.render(<SearchBox searchUrl="/api/search" detailsUrl="/api/details" />, document.getElementById('content'));
