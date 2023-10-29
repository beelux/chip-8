# CHIP-8 Interpreter - Trix

## Introduction
The freedom given to us for the bonus assignment might be freeing for some, but others might find it difficult to choose a project of appropriate size for the limited time.
The addition of creativity with the restriction of having to use Processing and Minim really made me worry about the feasibility of any bonus I'd do.

This is where CHIP-8 plays a role. As one of the listed suggestions on the Canvas page, it is similar to COPP's IJVM, and creative and visual enough for the bonus assignment.

Some context on CHIP-8:

> CHIP-8 is a simple, interpreted, programming language which was first used on some do-it-yourself computer systems in the late 1970s and early 1980s. The COSMAC VIP, DREAM 6800, and ETI 660 computers are a few examples. These computers typically were designed to use a television as a display, had between 1 and 4K of RAM, and used a 16-key hexadecimal keypad for input. The interpreter took up only 512 bytes of memory, and programs, which were entered into the computer in hexadecimal, were even smaller.
>
> (http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#1.0)

## Process and Notes

I implemented CHIP-8 as correctly and extensively as I could, yet limitations are still present:
- All test roms work
  - `5-quirks.ch8`: the display being "low" is the only complaint, but seemingly it is not a bug. 
- *Trying* to use Minim for sound was a horrible experience, and I was forced to remove it from my interpreter as I feared it would make it crash in unexpected ways.
  - It bugged out saying files did not exist when they did
  - The Oscillator (for custom square wave generation) was not working as well as needed: it did not turn on and off quirkly enough
    - at times, it slowed down the program to a crawl 
- Some roms are buggy (e.g. RPS), but they work well enough to demonstrate the interpreter's full functionality.
  - RPS has a bug where dual input might happen, I fear this might have to do with Processing and Java's AWT.

## Usage
### Run the interpreter
1. Update the `gradle.properties` file
   - Either:
     - delete it, and use Java 8
     - set it to your JAVA_HOME
   - It is currently set to my own (macOS) Java 8 installation
   - This is needed because I cannot use Java 8 as my main Java version, due to other applications needing newer versions.
2. If wanted, modify the `quirks.properties` file to a different ROM.
   - by default, it is set to `roms/3-corax+.ch8`, which is an extensive test rom
3. Run the `run` gradle task, or, as in Tetris, click on the green "play button" in `ChipVM.scala` inside of IntelliJ

I based my implementation mostly on the testing ROMs, the demo roms were not tested much and might be (very) buggy.

### Controls
The keypad is mapped to the following keys:
```
1234
QWER
ASDF
ZXCV
```

## ROMs
### Various Testing Roms
These are various testing roms that I found online and used to test the interpreter.
Many games might still be buggy, but this is because they might depend on specific quirks and behaviours I did not implement.

- `roms/1-chip8-logo.ch8`
- `roms/2-ibm-logo.ch8`
- `roms/3-corax+.ch8`
- `roms/4-flags.ch8`
- `roms/5-quirks.ch8`
- `roms/6-keypad.ch8`
- `roms/7-fontTest.ch8`

### Demos
These are demos that I found online, I did not make them myself, ***nor did I test them extensively.***


- `roms/pong.ch8` - Pong
  - `1` for P1 Up
  - `Q` for P1 Down
  - `4` for P2 Up
  - `R` for P2 Down
- `roms/br8kout.ch8` - Breakout
  - `A` for Left
  - `D` for Right
- `roms/tetris.ch8` - Tetris
  - `W` for Left
  - `E` for Right
  - `Q` for Rotate
- `roms/buggy-RoShamBo.ch8` - RoShamBo/RPS
  - Known bug: Display also is a bit wonky at times. It might rely on quirks or extended behaviours.
  - `Z` for Rock
  - `X` for Paper
  - `C` for Scissors
- `roms/sir.ch8` - Triangle
- `roms/war.ch8` - Press F to pay respects
  - `V` to pay respects 