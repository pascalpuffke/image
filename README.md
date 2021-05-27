# image
Command-line image manipulation tools

## Usage
JRE 16 is **required**, older versions are not supported. That fact alone should be enough for you not to consider actually using this thing.
And yes, I do use some newer language features.

````shell
java -jar image.jar [--input input_image] [--output output_image] [options]
````

Download a pre-compiled jar file from [Releases](https://github.com/pascalpuffke/image/releases) or use the maven ```package``` command.

I may eventually try using jpackage to compile regular executables, but for now you'll have to do it this way.

Options:

- ```-i```, ```--input <path>```
  
- ```-o```, ```--output <path>```
  
- ```-b```, ```--brighten <amount>```
  - Values range from 0 (no change) to 255 (clip everything white)
  
- ```-d```, ```--darken <amount>```
  - Values range from 0 (no change) to 255 (clip everything black)
  
- ```-f```, ```--filter <filter>```
  - See **Filters** section below

- ```-l```, ```--list-filters```
  
- ```-p```, ```--palette <path|text>```
  
- ```-r```, ```--resize <WxH>```
  
- ```-rq```, ```--resize-quick <WxH>```
  - About 5x quicker than regular ```resize```, though quality takes a hit for natural images
  
- ```--iterations <num>```
  - Higher amount approximates a Gaussian blur when using the ```box-blur``` filter
  
- ```--radius <pixels>```
  
- ```-h```, ```--help```
  - Help also shows when program is run without any arguments

- ```-V```, ```--version```

## Examples
Resize an image to full HD:
````shell
java -jar image.jar --input input.png --output output.png --resize 1920x1080
````

Blur an image using default settings:
````shell
java -jar image.jar --input input.png --output output.png --filter box_blur
````

Resize, brighten, blurring, dithering and grayscale an image (why would you do this?), all with custom settings and enabling debug messages:
````shell
java -jar image.jar \
--input input.png \
--output output.png \
--resize 1366x768 \
--brighten 20 \
--filter box_blur \
--radius 20 \
--iterations 2 \
--filter floyd_steinberg \
--palette palette.txt \
--filter grayscale \ 
--debug
````

## Filters

- Grayscale conversion
    - ```grayscale```, ```grayscale_average```, ```grayscale_min```, ```grayscale_max```, ```grayscale_perceptual```
  
- Palette conversion, dithering
  - ```floyd_steinberg```, ```nearest_color```
  
- Blurring
  - ```box_blur```
  
- Resizing
  - ```resize```, ```resize_fast```
  
- Brightness correction
  - ```brighten```, ```darken```

## Filter parameters

- Grayscale filters require no additional configuration.

- Palette conversion filters require a target palette; see **Using and formatting palettes** section below.
  - ```--palette <text|path>```

- Blur filters can take user-defined radius and iteration values, or fall back to standard values (radius=5, iterations=1).
  - ```--radius <pixels>```
  - ```--iterations <num>```
  
- Resize filters require either a target size in the form of ```WIDTHxHEIGHT``` or only the size of the longest side (calculates the smaller side to preserve the aspect ratio)
  - e.g. ```--resize 1920x1080``` or ```--resize 1920```

## Using and formatting palettes

In order to use palette conversion filters, this program requires a target palette.

You can either define palettes by writing them out as a command-line argument using the ```--palette``` flag,
or using a text file instead. Colours must be in hexadecimal form: #RRGGBB.

Supported formatting as a command-line argument:

```--palette "#FFFFFF #63D0E4 #000000"```

A text file should use newlines instead of whitespaces:

````text
#FFFFFF
#63D0E4
#000000
````

Use the text file by pointing the ```--palette``` flag to it.

```--palette path-to-file.txt```
