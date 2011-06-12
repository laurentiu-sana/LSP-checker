(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (p Bool) (q Bool) )
  :formula
    (or (or (implies p q) p) (not q))
)

