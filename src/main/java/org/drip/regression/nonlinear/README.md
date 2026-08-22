# DROP Regression Non-linear Package

<p align="center"><img src="https://github.com/lakshmiDRIP/DROP/blob/master/DRIP_Logo.gif?raw=true" width="100"></p>

DROP Regression Non-linear Package contains the Implementation of Non-linear Least Squares Regression Schemes.


## Class Components

 * [***GaussNewtonEstimator***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/GaussNewtonEstimator.java)
 <i>GaussNewtonEstimator</i> implements the Non-linear Least-Squares Regression using the Gauss-Newton Algorithm.

 * [***ImplicitTrustRegion***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/ImplicitTrustRegion.java)
 <i>ImplicitTrustRegion</i> holds the Implicit Trust Regions used in Non-linear Least Squares Regression.

 * [***LeastSquaresEstimator***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresEstimator.java)
 <i>LeastSquaresEstimator</i> exposes the Functionality for Non-linear Least-Squares Estimation.

 * [***LeastSquaresHessianControl***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresHessianControl.java)
 <i>LeastSquaresHessianControl</i> holds the Hessian Proxy and Line Search Settings used in the Least-Squares Hessian Algorithm.

 * [***LeastSquaresHessianEstimator***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresHessianEstimator.java)
 <i>LeastSquaresHessianEstimator</i> implements the Non-linear Least-Squares Regression using the Hessian of the Sum of Squared Residuals.

 * [***LeastSquaresIterationDiagnostics***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresIterationDiagnostics.java)
 <i>LeastSquaresIterationDiagnostics</i> holds the Results of a Least-Squares Iteration Run.

 * [***LeastSquaresRun***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresRun.java)
 <i>LeastSquaresRun</i> holds the Results of a Least-Squares Estimation Run.

 * [***LeastSquaresRunDiagnostics***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/LeastSquaresRunDiagnostics.java)
 <i>LeastSquaresRunDiagnostics</i> holds the Results of a Least-Squares Diagnostic Run.
 
 * [***R1R1Sample***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/R1R1Sample.java)
 <i>R1R1Sample</i> holds the Sample of (R<sup>1</sup>, R<sup>1</sup>) Realizations.
 
 * [***R1ToR1EnsembleResidualSquared***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/R1ToR1EnsembleResidualSquared.java)
 <i>R1ToR1EnsembleResidualSquared</i> holds the Squared Residuals corresponding to an Ensemble of (x, y) Pairs for a given Parameterized Objective Function.
 
 * [***R1ToR1Residual***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/R1ToR1Residual.java)
 <i>R1ToR1Residual</i> holds the Residual corresponding to a (x, y) Pair for a given Parameterized Objective Function.
 
 * [***SampleResidualJacobian***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/SampleResidualJacobian.java)
 <i>SampleResidualJacobian</i> holds the Gauss Newton "J" Matrix - the Array of Sample Residual Jacobians and their Transpose.
 
 
## References

 * Bjorck, A. (1996): <i>Numerical Methods for Least Squares Problems</i> <b>SIAM</b> Philadelphia PA

 * Dennis, J. E., and R. B. Schnabel (1983): <i>Numerical Methods for Unconstrained Optimization</i> <b>Prentice-Hall</b> Hoboken NJ

 * Madsen, K., H. B. Nielsen, and O. Tingleff (2004): Methods for Non-linear Least Squares Problems https://www2.imm.dtu.dk/pubdb/edoc/imm3215.pdf

 * Mascarenhas, W. F. (2013): The Divergence of the BGFS and the Gauss Newton Methods <i>Mathematical Programming</i> <b>147 (1)</b> 253-276

 * Nocedal, J., and S. Wright (1999): <i>Numerical Optimization</i> <b>Springer</b> New York NY

 * Wikipedia (2025): Gauss-Newton Method https://en.wikipedia.org/wiki/Gauss%E2%80%93Newton_algorithm

 * Wikipedia (2025): Trust Region https://en.wikipedia.org/wiki/Trust_region


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
