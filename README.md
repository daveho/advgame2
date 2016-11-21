# Adventure game in ClojureScript?

This is an attempt to write an adventure game in ClojureScript.

## Trying it out

Run the command

    ./scripts/brepl

and then navigate your web browser to [localhost:9000](http://localhost:9000).  The arrow keys move.  Note that no actual gameplay is implemented at this point.

## Project structure

The `src-model` directory contains all of the UI-independent code.  This code can be tested.

The `src` directory contains all of the UI (browser-based) code.

## Testing

Make sure that `phantomjs` is installed.

Run `./runtests.sh` to run the tests.

## Code used

[Tincan](https://github.com/nhusher/tincan), license is in [LICENSE-tincan.txt](LICENSE-tincan.txt).

[perlin](https://github.com/indy/perlin), license is in [LICENSE-perlin.txt](LICENSE-perlin.txt).

Testing support is copied shamelessly from [basic setup for cljs.test](https://keeds.github.io/clojurescript/2014/12/19/cljs-test.html), code is at <https://gitlab.com/keeds/cljsinit>.
