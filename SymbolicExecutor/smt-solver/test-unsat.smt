(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (p Bool) (q Bool) )
  :formula
    (and (and (implies p q) p) (not q))
)

