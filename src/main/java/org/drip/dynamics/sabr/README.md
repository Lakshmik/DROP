# DROP Dynamics SABR Package

<p align="center"><img src="https://github.com/lakshmiDRIP/DROP/blob/master/DRIP_Logo.gif?raw=true" width="100"></p>

DROP Dynamics SABR Package implements the SABR Based Latent State Evolution.


## Class Components

 * [***AntonovKonikovSpector2015***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/AntonovKonikovSpector2015.java)
 <i>AntonovKonikovSpector2015</i> implements the Anotonov, Konikov, and Spector (2015) Variant of the SABR C Function.

 * [***CFunction***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/CFunction.java)
 <i>CFunction</i> exposes the Variants of the SABR C Function.

 * [***CFunctionClassical***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/CFunctionClassical.java)
 <i>CFunctionClassical</i> implements the Classical SABR C Function.

 * [***EuropeanOptionSetting***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/EuropeanOptionSetting.java)
 <i>EuropeanOptionSetting</i> holds the Strike/TTE of a European Option.

 * [***ForwardProcessSetting***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/ForwardProcessSetting.java)
 <i>ForwardProcessSetting</i> contains the Settings that determine the SABR Dynamics.

 * [***ForwardUpdate***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/ForwardUpdate.java)
 <i>ForwardUpdate</i> contains the Increment and Snapshot of the Forward Rate Latent State evolved through the SABR Dynamics.

 * [***HaganKumarLesniewskiWoodward2002***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/HaganKumarLesniewskiWoodward2002.java)
 <i>HaganKumarLesniewskiWoodward2002</i> executes a Volatility Implication Run using the Hagan, Kumar, Lesniewski, and Woodward (2002).

 * [***StartingStateRealization***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/StartingStateRealization.java)
 <i>StartingStateRealization</i> contains the Initial Forward Rate and its Volatility.

 * [***StochasticVolatilityStateEvolver***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/StochasticVolatilityStateEvolver.java)
 <i>StochasticVolatilityStateEvolver</i> provides the SABR Stochastic Volatility Evolution Dynamics.

 * [***VolatilityImplication***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/VolatilityImplication.java)
 <i>VolatilityImplication</i> maintains the Results of Volatility Implication Run.

 * [***Wang2010***](https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/Wang2010.java)
 <i>Wang2010</i> implements Beta Estimation using Linear Regression from ATM Implied Volatilities to Initial Forward Rates Time Series.


# References

 * Choi, J., and L. Wu (2021): The Equivalent Constant Elasticity-of-Variance (CEV) Volatility of the Stochastic Alpha-Beta-Rho (SABR) Model <i>Journal of Economic Dynamics and Control</i> <b>128</b> 104143

 * Grzelak, L. A., and C. W. Oosterlee (2016): From Arbitrage to Arbitrage-free Implied Volatilities <i>Journal of Computational Finance</i> <b>20 (3)</b> 31-49

 * Guerrero, J., and G. Orlando (2021): Stochastic Local Volatility Models and the Wei-Normal Factorization Method <i>Discrete and Continuous Dynamical Systems – S</i> <b>15 (12)</b> 3699-3722

 * Hagan, P. S., D. Kumar, A. S. Lesniewski, and D. E. Woodward (2002): Managing Smile Risk <i>Wilmott</i> <b>1</b> 84-108

 * Wikipedia (2026): SABR Volatility Model https://en.wikipedia.org/wiki/SABR_volatility_model


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
