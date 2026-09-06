# DROP Function R<sup>1</sup> To R<sup>1</sup> Custom Package

<p align="center"><img src="https://github.com/lakshmiDRIP/DROP/blob/master/DRIP_Logo.gif?raw=true" width="100"></p>

DROP Function R<sup>1</sup> To R<sup>1</sup> Custom Package contains the Built-in R<sup>1</sup> To R<sup>1</sup> Custom Functions.


## Class Components

 * [***AlmgrenEnhancedEulerUpdate***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/AlmgrenEnhancedEulerUpdate.java)
 <i>AlmgrenEnhancedEulerUpdate</i> is a R<sup>1</sup> To R<sup>1</sup> Function that is used in Almgren (2009, 2012) to illustrate the Construction of the Enhanced Euler Update Scheme.

 * [***AndersenPiterbargMeanReverter***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/AndersenPiterbargMeanReverter.java)
 <i>AndersenPiterbargMeanReverter</i> implements the mean-reverting Univariate Function detailed in Andersen and Piterbarg (2010).

 * [***CIRPDF***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/CIRPDF.java)
 <i>CIRPDF</i> exposes the R<sup>1</sup> Univariate Cox-Ingersoll-Ross Probability Density Function.

 * [***ISDABucketCurvatureTenorScaler***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/ISDABucketCurvatureTenorScaler.java)
 <i>ISDABucketCurvatureTenorScaler</i> generates the ISDA SIMM Tenor Scaling Factor for a given Bucket Curvature.

 * [***LinearRationalShapeControl***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/LinearRationalShapeControl.java)
 <i>LinearRationalShapeControl</i> implements the deterministic rational shape control functionality on top of the Estimator basis splines inside - [0,...,1) - Globally [x_0,...,x_1)

 * [***LinearRationalTensionExponential***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/LinearRationalTensionExponential.java)
 <i>LinearRationalTensionExponential</i> provides the evaluation of the Convolution of the Linear Rational and the Tension Exponential Functions and its derivatives for a specified variate

 * [***QuadraticRationalShapeControl***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/QuadraticRationalShapeControl.java)
 <i>QuadraticRationalShapeControl</i> implements the deterministic rational shape control functionality on top of the Estimator basis splines inside - [0,...,1) - Globally [x_0,...,x_1)

 * [***SABRLIBORCapVolatility***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/function/r1tor1custom/SABRLIBORCapVolatility.java)
 <i>SABRLIBORCapVolatility</i> implements the Deterministic, Non-local Cap Volatility Scheme detailed in Rebonato, McKay, and White (2009)


## References

 * Almgren, R. F., and N. Chriss (2000): Optimal Execution of Portfolio Transactions <i>Journal of Risk</i> <b>3 (2)</b> 5-39

 * Almgren, R. F. (2009): Optimal Trading in a Dynamic Market https://www.math.nyu.edu/financial_mathematics/content/02_financial/2009-2.pdf

 * Almgren, R. F. (2012): Optimal Trading with Stochastic Liquidity and Volatility <i>SIAM Journal of Financial Mathematics</i> <b>3 (1)</b> 163-181

 * Andersen and Piterbarg (2010): Interest Rate Modeling (3 Volumes), Atlantic Financial Press.

 * Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Credit Exposure in the Presence of Initial Margin https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2806156 <b>eSSRN</b>

 * Albanese, C., S. Caenazzo, and O. Frankel (2017): Regression Sensitivities for Initial Margin Calculations https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2763488 <b>eSSRN</b>

 * Anfuso, F., D. Aziz, P. Giltinan, and K. Loukopoulus (2017): A Sound Modeling and Back-testing Framework for Forecasting Initial Margin Requirements https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2716279 <b>eSSRN</b>

 * Bogoliubov, N. N., and D. P. Sankevich (1994): N. N. Bogoliubov and Statistical Mechanics <i>Russian Mathematical Surveys</i> <b>49 (5)</b> 19-49

 * Caspers, P., P. Giltinan, R. Lichters, and N. Nowaczyk (2017): Forecasting Initial Margin Requirements - A Model Evaluation https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2911167 <b>eSSRN</b>

 * Holubec, V., K. Kroy, and S. Steffenoni (2019): Physically Consistent Numerical Solver for Time-dependent Fokker-Planck Equations <i>Physical Review E</i> <b>99 (4)</b> 032117

 * International Swaps and Derivatives Association (2017): SIMM v2.0 Methodology https://www.isda.org/a/oFiDE/isda-simm-v2.pdf

 * Kadanoff, L. P. (2000): <i>Statistical Physics: Statics, Dynamics, and Re-normalization</i> <b>World Scientific</b>

 * Ottinger, H. C. (1996): <i>Stochastic Processes in Polymeric Fluids</i> <b>Springer-Verlag</b> Berlin-Heidelberg

 * Rebonato, R., K. McKay, and R. White (2009): <i>The SABR/LIBOR Market Model: Pricing, Calibration, and Hedging for Complex Interest-Rate Derivatives</i> <b>John Wiley and Sons</b>

 * Wikipedia (2019): Fokker-Planck Equation https://en.wikipedia.org/wiki/Fokker%E2%80%93Planck_equation


## DROP Specifications

 * Main                     => https://lakshmidrip.github.io/DROP/
 * Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * GitHub                   => https://github.com/lakshmiDRIP/DROP
 * Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
