# circular.zoetrope
Create animated zoopraxiscope images from original discs. Search Wiki Commons for "optical illusion disc" for examples.

This animates an optical illusion disc. These discs were popular in the 19th century, starting around the 1830s, and 
were the first kind of moving image. This makes them a distant ancestor of both modern motion pictures and animation.

# Usage

This creates a new animated .gif file from an existing Optical Illusion disc.

With your class path set properly, here is the usage:

    java com.neptunedreams.zoetrope.Zoetrope <source-file> <frame-count> [<frames-per-second> [<size>]]

frames-per-second defaults to 10

size defaults to the original size, and is measured in pixels.

If the animation plays backwards, negate the frame count.

Examples: (Assume you have a command `animate` which translates to `java com.neptunedreams.zoetrope.Zoetrope`)

    animate jumping-dog.jpg 10          ! animate with 10 frames
    animate jumping-dog.jpg -10         ! animate with 10 frames, in the opposite direction
    animate jumping-dog.jpg 10 12       ! animate with 10 frames and 12 frames per second
    animate jumping-dog.jpg 10 12 200   ! animate with 10 frames, 12 fps, and produce a 200 x 200 animation

Caveats:

1. The source image should be a perfect square.

2. A positive frame number spins the disc counter-clockwise. A negative number spins clockwise. 

# Examples:

With a source file like this:

![Horse and rider image](https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Optical_illusion_disc_with_somersaults_and_horseback_riding_LCCN00651161.jpg/598px-Optical_illusion_disc_with_somersaults_and_horseback_riding_LCCN00651161.jpg)

You can clip out the image, and (with a bit of color adjustment) get this:

![Animated horse and rider](https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Optical_illusion_disc_with_somersaults_and_horseback_riding-whiteBalance-1000p.gif/480px-Optical_illusion_disc_with_somersaults_and_horseback_riding-whiteBalance-1000p.gif)

In the 1830s, to view the animation, the static image would be printed on a wheel that you could spin. You needed to turn the
image to face a mirror, spin the image, then look through the open slots at the reflected image. In this example, the disc
spins counter-clockwise, which produces an illusion of a horse running clockwise.
