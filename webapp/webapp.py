import os
import glob
import time
import streamlit as st
import requests
from PIL import Image
import generate_frames as gen_frames

st.title("🔥 Wildfire Simulation")

# path for the simulation frames
static_root = "./static"
os.makedirs(static_root, exist_ok=True)

# form to store grid parameters
with st.form("grid_form"):
    st.subheader("Initialisation de la grille")
    size = st.number_input("Taille de la grille", min_value=1, value=50)
    humidity = st.slider("Humidité", 0.0, 1.0, 0.3)
    temperature = st.number_input("Température", value=20.0)
    wind_speed = st.number_input("Vitesse du vent", value=10.0)
    wind_dir = st.selectbox("Direction du vent", ["N", "E", "S", "W", "NE", "NW", "SE", "SW"])
    submit_grid = st.form_submit_button("Valider les paramètres")

if submit_grid:
    grid_params = {
        "gridSize": size,
        "humidity": humidity,
        "temperature": temperature,
        "windSpeed": wind_speed,
        "windDirection": wind_dir
    }
    requests.post("http://localhost:8080/submit-grid-params", json=grid_params)
    st.success("Grille initialisée")

# form to store simulation parameters
with st.form("simu_form"):
    st.subheader("Lancer la simulation")
    duration = st.number_input("Durée (nombre d'étapes)", min_value=1, value=100)
    blitX = st.number_input("Point d’allumage X", min_value=0, value=10)
    blitY = st.number_input("Point d’allumage Y", min_value=0, value=10)
    submit_simu = st.form_submit_button("Lancer simulation")

if submit_simu:
    simu_params = {"duration": duration, "blitzX": blitX, "blitzY": blitY}
    res = requests.post("http://localhost:8080/submit-simu-params", json=simu_params)
    frames = res.json()
    new_folder = gen_frames.save_simulation_frames(frames)
    st.session_state["selected_sim"] = os.path.basename(new_folder)
    st.success(f"Simulation enregistrée : {st.session_state['selected_sim']}")

# choose a simulation from the list of directories
sim_dirs = sorted(
    [d for d in os.listdir(static_root) if d.startswith("simulation_") and d.split("_")[1].isdigit()],
    key=lambda x: int(x.split("_")[1])
)

if sim_dirs:
    default_idx = sim_dirs.index(st.session_state.get("selected_sim", sim_dirs[0]))
    selected_sim = st.selectbox("Choisir une simulation", sim_dirs, index=default_idx)
    st.session_state["selected_sim"] = selected_sim

    # show the selected simulation frames
    folder = os.path.join(static_root, st.session_state["selected_sim"])
    images = sorted(glob.glob(os.path.join(folder, "frame_*.png")))

    if images:
        # manual slider to select the frame index
        idx = st.slider("Étape", 0, len(images) - 1, 0)

        # play button to start the simulation
        if st.button("▶️ Play"):
            placeholder = st.empty()
            progress = st.progress(0)
            total = len(images)
            for j in range(idx, total):
                img = Image.open(images[j])
                placeholder.image(img, use_container_width=True)
                progress.progress((j + 1) / total)
                time.sleep(1)
            progress.empty()
        else:
            # show the selected frame
            st.image(Image.open(images[idx]), use_container_width=True)

