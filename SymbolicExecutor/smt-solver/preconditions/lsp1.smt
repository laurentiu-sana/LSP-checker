(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :extrafuns ( (y Int) )
  :formula
  ; Subclass: x < 0
  ; Superclass: x < 10 && y > 0
  (not (implies (not (< x 0)) (not (and (< x 10) (> y 0)))))
)

