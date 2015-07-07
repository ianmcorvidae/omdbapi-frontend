var React = require('react');
var $ = require('jquery');

var SearchForm = React.createClass({
  handleSubmit: function(e) {
    e.preventDefault();
    var query = React.findDOMNode(this.refs.query).value.trim();
    var year = React.findDOMNode(this.refs.year).value.trim();
    if (!query) { return; }
    this.props.onSubmit(query, year);
    return;
  },
  render: function() {
    return (
      <form className="searchForm" onSubmit={this.handleSubmit}>
        <input type="text" name="query" ref="query" placeholder="Movie Title" />
        <input type="text" name="year" ref="year" placeholder="YYYY" />
        <input type="submit" />
      </form>
    );
  }
});
var SearchDetails = React.createClass({
  render: function() {
    return (
      <p>{this.props.data.omdb.join(', ')}</p>
    );
  }
});
var SearchResult = React.createClass({
  getInitialState: function() {
    return {expanded: false, expandedData: null};
  },
  handleClick: function(e) {
    if (this.state.expanded) {
      this.setState({expanded: false});
    } else {
      this.setState({expanded: true});

      if (!this.state.expandedData) {
        $.ajax({
          url: this.props.url,
          data: {id: this.props.data[2]},
          type: 'GET',
          success: function(data) {
            this.setState({expandedData: data});
          }.bind(this),
          error: function(xhr, status, err) {
            console.error(this.props.url, status, err.toString());
          }.bind(this)
        });
      }
    }
  },
  render: function() {
    var more;
    if (this.state.expanded && this.state.expandedData) {
      more = <SearchDetails data={this.state.expandedData} />;
    }
    return (
      <tr>
        <td className="expand" title="Show More" onClick={this.handleClick}>&#43;</td>
        <td className="title">{this.props.data[0]}{more}</td>
        <td className="year">{this.props.data[1]}</td>
        <td className="imdb"><a href={"http://www.imdb.com/title/" + this.props.data[2]} target="_blank">{this.props.data[2]}</a></td>
      </tr>
    );
  }
});
var SearchBox = React.createClass({
  getInitialState: function() {
    return {results: []};
  },
  handleSearchSubmit: function(query, year) {
    $.ajax({
      url: this.props.searchUrl,
      data: {q: query, y: year},
      type: 'GET',
      success: function(data) {
        this.setState({results: data.omdb});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.searchUrl, status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    var results;
    var detailsUrl = this.props.detailsUrl;
    if (this.state.results && this.state.results.length > 0) {
      var rows = this.state.results.map(function (r) { return (<SearchResult data={r} url={detailsUrl} />); });
      results = (
          <table>
            <thead><tr><th></th><th>Title</th><th>Year</th><th>IMDB ID</th></tr></thead>
            <tbody>{rows}</tbody>
          </table>
      );
    } else if (this.state.results === null) {
      results = (<p>No results found, or server error. Please try again.</p>);
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
