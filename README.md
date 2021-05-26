# image
Command-line image manipulation tools

## Features

- Grayscale conversion
    - ```grayscale```, ```grayscale_average```, ```grayscale_min```, ```grayscale_max```, ```grayscale_perceptual```
  
- Palette conversion, dithering
  - ```floyd_steinberg```, ```nearest_color```
  
- Blurring
  - ```box_blur```
  
- Resizing
  - ```resize```, ```resize_fast```, ```resize_quality```
  
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