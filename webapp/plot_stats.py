import sys
import re
import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

# Fonction sigmoïde standard
def sigmoid(x, L, x0, k, b):
    return L / (1 + np.exp(-k * (x - x0))) + b

# Fonction sigmoïde inversée
def inverted_sigmoid(x, L, x0, k, b):
    return L / (1 + np.exp(k * (x - x0))) + b

def parse_file(filepath):
    with open(filepath, 'r', encoding="latin-1") as f:
        lines = f.readlines()

    # Extraire le paramètre étudié
    param_line = next((l for l in lines if "Paramètre étudié" in l), None)
    param_match = re.search(r"Paramètre étudié\s*:\s*(\w+)", param_line)
    param = param_match.group(1) if param_match else "inconnu"

    # Extraire les paires (valeur, pourcentage)
    pattern = re.compile(rf"{param}\s*=\s*(-?\d+)\s*->\s*([\d.,]+)%")
    x_vals, y_vals = [], []
    for line in lines:
        match = pattern.search(line)
        if match:
            x_vals.append(int(match.group(1)))
            y_vals.append(float(match.group(2).replace(",", ".")))

    return param, np.array(x_vals), np.array(y_vals)

def fit_and_plot(filepath):
    param, x, y = parse_file(filepath)

    # Choisir la bonne fonction selon le paramètre
    decreasing = param.lower() in ["humidity", "humidite"]
    model = inverted_sigmoid if decreasing else sigmoid

    # Estimations initiales pour l'ajustement
    L_init = max(y) - min(y)
    x0_init = x[np.argmax(np.gradient(y))]  # estimation inflexion brute
    k_init = 0.3
    b_init = min(y)

    try:
        popt, _ = curve_fit(model, x, y, p0=[L_init, x0_init, k_init, b_init], maxfev=10000)
    except RuntimeError:
        print("❌ Ajustement échoué.")
        return

    L, x0, k, b = popt
    x_fit = np.linspace(min(x), max(x), 500)
    y_fit = model(x_fit, *popt)

    # Affichage
    plt.figure(figsize=(10, 6))
    plt.plot(x, y, "o", label="Données simulées", color="crimson")
    plt.plot(x_fit, y_fit, "-", label="Ajustement sigmoïde", color="navy")
    plt.axvline(x=x0, color="green", linestyle="--", label=f"Inflexion ≈ {x0:.2f} {param}")
    plt.title(f"% brûlé en fonction de {param}")
    plt.xlabel(param.capitalize())
    plt.ylabel("Pourcentage brûlé (%)")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    fit_and_plot("C:\\Users\\Loicc\\OneDrive\\Bureau\\physics_project\\simulation\\results_varying_wind.txt")
