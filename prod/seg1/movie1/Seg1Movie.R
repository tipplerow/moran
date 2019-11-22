
Seg1Movie.genPNGs <- function(coordFile = "genotype-coord.csv") {
    coordFrame <- read.csv(coordFile)
    timeSteps  <- sort(unique(coordFrame$stepIndex))

    for (timeStep in timeSteps)
        Seg1Movie.genPNG(coordFrame, timeStep)
}

Seg1Movie.genPNG <- function(coordFrame, timeStep, pngFile, cex = 2.0) {
    if (missing(pngFile))
        pngFile <- sprintf("step%06d.png", timeStep)

    JamLog.info("Processing time step [%d]...", timeStep)

    slice <- subset(coordFrame, stepIndex == timeStep)
    xmax  <- 1.2 * max(slice$x)
    ymax  <- 1.2 * max(slice$y)
    
    plot(slice$x, slice$y,
         axes = FALSE,
         xlab = "",
         ylab = "",
         xlim = c(0, xmax),
         ylim = c(0, ymax),
         type = "p",
         pch  = 16,
         cex = cex,
         col  = 1 + slice$seg1)

    legend("topright", bty = "n",
           legend = as.character(0:6),
           cex = 2.0,
           col = 1:7,
           pch = rep(16, 7))

    dev.copy(png, file = pngFile, width = 1600, height = 1600)
    dev.off()
}
