# omdbapi-frontend

A basic interface for accessing the API provided by http://www.omdbapi.com and
displaying related information.

This is primarily a toy project. It's also my first Clojure project outside of
toying around in a REPL; be gentle.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

Using standard processes it should be possible to install the other
dependencies using leiningen via `lein deps`.

You will also need npm, the node package manager, if you'd like to change any
jsx code; precompiled versions are provided for convenience.  Install
dependencies with `npm install` and build javascript with `npm run js`.

## Running

To start a web server for the application, run:

    lein ring server

## Known Imperfections

 * Compiled JS is checked into the repository. Probably better otherwise, but
   this seems a lot nicer for running quickly for Clojure users.
 * Build tools: this uses npm as a build tool as recommended by
   http://blog.keithcirkel.co.uk/how-to-use-npm-as-a-build-tool/, but something
   like Gulp or Grunt might be more practical.
 * `location.hash` handling is rather rudimentary and could benefit from using
   a proper library if made any more complex
 * webfonts could probably be better-optimized
 * styling is quite sparse; design takes more time than code for me!

## License

Copyright (C) 2015 Ian McEwen

This work is available under the terms of the WTFPL, whose complete text follows:

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                        Version 2, December 2004
    
     Copyright (C) 2004 Sam Hocevar
     
     Everyone is permitted to copy and distribute verbatim or modified 
     copies of this license document, and changing it is allowed as long 
     as the name is changed. 
    
                DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
       TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 
    
      0. You just DO WHAT THE FUCK YOU WANT TO.
