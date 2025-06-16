from pathlib import Path
import imageio.v2 as imageio

def generate_video_from_frames(input_dir, output_path="output.mp4", duration_sec=15, fps=60):
    input_dir = Path(input_dir)
    if not input_dir.is_dir():
        raise ValueError(f"Le dossier {input_dir} n'existe pas.")

    frames = sorted([f for f in input_dir.iterdir() if f.suffix in ['.png', '.jpg', '.jpeg']])
    n_original = len(frames)
    total_frames = duration_sec * fps

    if n_original == 0:
        raise ValueError("Aucune image trouvée dans le dossier.")

    base_dup = total_frames // n_original
    extra = total_frames % n_original

    print(f"{n_original} frames trouvées.")
    print(f"Chaque image sera dupliquée {base_dup} fois (+1 pour les {extra} premières).")

    all_frames = []
    counter = 0
    for i, frame in enumerate(frames):
        dup = base_dup + (1 if i < extra else 0)
        img = imageio.imread(frame)
        all_frames.extend([img] * dup)
        counter += dup

    imageio.mimsave(output_path, all_frames, fps=fps)
    print(f"Vidéo générée : {output_path}")

generate_video_from_frames("C:\\Users\\Loicc\\OneDrive\\Bureau\\physics_project\\webapp\\static\\simulation_5", "simulation.mp4")
